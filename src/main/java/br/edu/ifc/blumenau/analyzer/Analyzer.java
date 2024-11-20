package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import io.github.cdimascio.dotenv.Dotenv;
import com.github.javaparser.ast.CompilationUnit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Analyzer {

    public static final String DESCRICAO_ERRO_PADRAO = "Insira uma descrição de erro significativo aqui";
    private  final AtomicInteger totalAsserts = new AtomicInteger(0);
    private  final AtomicInteger totalAssertsSemDesc = new AtomicInteger(0);
    private  final AtomicInteger totalAssertsComDesc = new AtomicInteger(0);
    private  final AtomicInteger totalAssertionRoulette = new AtomicInteger(0);
    private  final ArrayList<MethodDeclaration> metodosTesteChamados = new ArrayList<>();
    private  ArrayList<String> assertComUmParametro = new ArrayList<>();
    private  String openSourceProjectsDir;
    private int contador;

    private CombinedTypeSolver combinedTypeSolver;
    private JavaSymbolSolver symbolSolver;

    public void run() {
        this.contador = 0;
        assertComUmParametro.add("assertTrue");
        assertComUmParametro.add("assertFalse");
        assertComUmParametro.add("assertNull");
        assertComUmParametro.add("assertNotNull");

        Dotenv dotenv = Dotenv.load();
        openSourceProjectsDir = dotenv.get("PROJECTS_DIR");

        Path projectDir = Paths.get(openSourceProjectsDir);

        System.out.println("\nProjeto: " + openSourceProjectsDir + "\n");

        StaticJavaParser.getConfiguration().setAttributeComments(false);

        try {
            Files.walk(projectDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(this::analyzeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Total asserts: " + totalAsserts);
        System.out.println("Asserts sem descrição: " + totalAssertsSemDesc);
        System.out.println("Asserts com descrição: " + totalAssertsComDesc);
        System.out.println("Assertion Roulette: " + metodosTesteChamados.size());
    }


    private void analyzeFile(Path path) {
        try (FileInputStream in = new FileInputStream(path.toFile())) {

            CompilationUnit compilationUnit;
            try {
                compilationUnit = StaticJavaParser.parse(in);
            }
            catch (Exception e) {
                return;
            }

            if (compilationUnit == null) {
                return;
            }

            Map<String, String> assertImports = new HashMap<>();

            mapAssertImports(compilationUnit, assertImports);

            MethodVisitor methodVisitor = new MethodVisitor(totalAsserts, totalAssertsSemDesc, totalAssertsComDesc, totalAssertionRoulette, metodosTesteChamados);
            compilationUnit.accept(methodVisitor, path);

            for (MethodDeclaration method: metodosTesteChamados) {
                method.getBody().ifPresent(body -> {
                    refactorAsserts(body, assertImports);
                });
            }

            printMethodsAfterRefactor();
            writeToFile(path, compilationUnit);

        } catch (IOException ignored) {

        }
    }

    private void refactorAsserts(BlockStmt body, Map<String, String> assertImports) {
        List<Statement> statements = body.getStatements();
        List<Statement> newStatements = new ArrayList<>();

//        for (Statement stmt : body.getStatements()) {
        for (int i=0; i< statements.size(); i++) {
            Statement stmt = statements.get(i);

            if (!(stmt.isExpressionStmt())) continue;

            ExpressionStmt exprStmt = stmt.asExpressionStmt();

            if (!(exprStmt.getExpression() instanceof MethodCallExpr methodCall)) {
                continue;
            }

            if (!methodCall.getNameAsString().startsWith("assert")) {
                continue;
            }

            int qtdParametros = methodCall.getArguments().size();
            String nomeMetodo = methodCall.getName().asString();
            boolean isAssertionSemDescricao = qtdParametros < (assertComUmParametro.contains(nomeMetodo) ? 2 : 3);

            if (!isAssertionSemDescricao) {
                continue;
            }

            if (assertImports.get(nomeMetodo) != null && assertImports.get(nomeMetodo).equals(AssertOrigin.JUNIT_5.getValue())
                    || assertImports.get(nomeMetodo) != null && assertImports.get(nomeMetodo).equals(AssertOrigin.JUNIT_4.getValue())) {

                Expression param1 = methodCall.getArgument(0);
                Expression param2 = (qtdParametros == 2) ? methodCall.getArgument(1) : null;

                Expression mensagemAssert = criarMensagemAssert(nomeMetodo, param2, param1, body, newStatements);
                MethodCallExpr novaChamada = new MethodCallExpr(nomeMetodo);

                if (assertImports.get(nomeMetodo).equals(AssertOrigin.JUNIT_5.getValue())) {
                    novaChamada = refactorJunit5(novaChamada, param1, param2, mensagemAssert);
                } else {
                    novaChamada = refactorJunit4(novaChamada, param1, param2, mensagemAssert);
                }

                statements.addAll(i, newStatements);
                stmt.replace(new ExpressionStmt(novaChamada));
                newStatements.clear();
            } else {
                System.out.println("refatoração não suportada para método: " + nomeMetodo);
            }
        }

        // Apply the new statements after the iteration is done
        for (Statement newStmt : newStatements) {
            body.addStatement(0, newStmt);  // Add new statements at the start of the body
        }
    }


    private MethodCallExpr refactorJunit4(MethodCallExpr newMethodCall, Expression param1, Expression param2, Expression mensagemAssert) {
        newMethodCall = newMethodCall.addArgument(mensagemAssert);
        newMethodCall = newMethodCall.addArgument(param1);
        if (param2 != null) {
            newMethodCall = newMethodCall.addArgument(param2);
        }
        return newMethodCall;
    }

    private MethodCallExpr refactorJunit5(MethodCallExpr newMethodCall, Expression param1, Expression param2, Expression mensagemAssert) {
        newMethodCall = newMethodCall.addArgument(param1.toString());
        if (param2 != null) {
            newMethodCall = newMethodCall.addArgument(param2.toString());
        }
        newMethodCall = newMethodCall.addArgument(mensagemAssert);
        return newMethodCall;
    }

    @NotNull
    private Expression criarMensagemAssert(String nomeMetodo, Expression param2, Expression param1, BlockStmt body, List<Statement> newStatements) {
        String mensagemAssert = "";

        String nomeVar1 = "";
        String padraoNomeVarTemp = "result";
        if (param1 instanceof MethodCallExpr param1AsMethodCall) {
            Type stringType = new ClassOrInterfaceType(null, "String");
            nomeVar1 = padraoNomeVarTemp + String.valueOf(this.contador++);

            MethodCallExpr valorVar = new MethodCallExpr("String.valueOf");
            valorVar.addArgument(param1AsMethodCall);
            VariableDeclarator newVar = new VariableDeclarator(stringType, nomeVar1, valorVar);
            VariableDeclarationExpr declaration = new VariableDeclarationExpr(newVar);
            ExpressionStmt expression = new ExpressionStmt(declaration);
            newStatements.add(expression);
        }

        String nomeVar2 = "";
        if (param2 instanceof MethodCallExpr param2AsMethodCall) {
            Type stringType = new ClassOrInterfaceType(null, "String");
            nomeVar2 = padraoNomeVarTemp + String.valueOf(this.contador++);

            MethodCallExpr valorVar = new MethodCallExpr("String.valueOf");
            valorVar.addArgument(param2AsMethodCall);
            VariableDeclarator newVar = new VariableDeclarator(stringType, nomeVar2, valorVar);
            VariableDeclarationExpr declaration = new VariableDeclarationExpr(newVar);
            ExpressionStmt expression = new ExpressionStmt(declaration);
            newStatements.add(expression);
        }

        if (nomeMetodo.equals("assertEquals")) {
            return criarMensagemAssertEquals(param2, param1, nomeVar1, nomeVar2);
        } else {
            mensagemAssert = DESCRICAO_ERRO_PADRAO;
        }
        return new StringLiteralExpr(mensagemAssert);
    }

    @NotNull
    private static Expression criarMensagemAssertEquals(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado valores iguais, mas ";
        String mensagemParte2 = String.format(" <%s> é diferente de ", param1.toString());
        String mensagemParte3 = String.format(" <%s>", param2.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            String parte2Formatada = mensagemParte2.replace("<", "").replace(">", "");
            result = new StringLiteralExpr(mensagemParte1 + parte2Formatada);
        }

        if (!nomeVar2.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar2), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte3), BinaryExpr.Operator.PLUS);
        } else {
            String mensagemSemMaiorEMenor = mensagemParte3.replace("<", "").replace(">", "");
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemSemMaiorEMenor), BinaryExpr.Operator.PLUS);
        }

        return result;
    }

    private void mapAssertImports(CompilationUnit compilationUnit, Map<String, String> assertImports) {
        compilationUnit.getImports().forEach(importDeclaration -> {

            if (!importDeclaration.isStatic()) {
                return;
            }

            String importAsString = importDeclaration.getNameAsString();

            if (importAsString.contains("assert") || importAsString.contains("fail")) {
                String methodName = importDeclaration.getName().getIdentifier(); // Nome do método
                String origin = getImportOrigin(importAsString);
                assertImports.put(methodName, origin);
                // System.out.println("Import direto: " + methodName + " de " + importAsString);
            } else if (importAsString.contains("org.junit.jupiter.api.Assertions") && importDeclaration.isAsterisk()) {
                // System.out.println("importa todos os asserts do Junit 5");
                addAllJunit5MethodsToMap(assertImports);
            } else if (importAsString.contains("org.junit.Assert") && importDeclaration.isAsterisk()) {
                // System.out.println("importa todos os asserts do Junit 4");
                addAllJunit4MethodsToMap(assertImports);
            } else if (importAsString.contains("org.assertj.core.api.Assertions") && importDeclaration.isAsterisk()) {
                // System.out.println("importa todos os asserts do assertJ");
                addAllAssertJMethodsToMap(assertImports);
            } else if (importAsString.contains("org.hamcrest.MatcherAssert") && importDeclaration.isAsterisk()) {
                // System.out.println("importa todos os asserts do hamcrest");
                addAllHamcrestMethodsToMap(assertImports);
            }
        });
    }

    private void addAllHamcrestMethodsToMap(Map<String, String> assertImports) {
        for (HamcrestAsserts assertMethod : HamcrestAsserts.values()) {
            assertImports.put(assertMethod.getMethodName(), AssertOrigin.HAMCREST.getValue());
        }
    }

    private void addAllAssertJMethodsToMap(Map<String, String> assertImports) {
        for (AssertJAsserts assertMethod : AssertJAsserts.values()) {
            assertImports.put(assertMethod.getMethodName(), AssertOrigin.ASSERTJ.getValue());
        }
    }

    private void addAllJunit4MethodsToMap(Map<String, String> assertImports) {
        for (Junit4Asserts assertMethod : Junit4Asserts.values()) {
            assertImports.put(assertMethod.getMethodName(), AssertOrigin.JUNIT_4.getValue());
        }
    }

    @NotNull
    private String getImportOrigin(String importAsString) {
        String origin;

        if (importAsString.contains("org.junit.Assert")) {
            origin = AssertOrigin.JUNIT_4.getValue();
        } else if (importAsString.contains("org.junit.jupiter.api.Assertions")) {
            origin = AssertOrigin.JUNIT_5.getValue();
        } else if (importAsString.contains("org.assertj.core.api.Assertions")) {
            origin = AssertOrigin.ASSERTJ.getValue();
        } else if (importAsString.contains("org.hamcrest.MatcherAssert")) {
            origin = AssertOrigin.HAMCREST.getValue();
        } else {
            origin = AssertOrigin.DESCONHECIDO.getValue();
        }
        return origin;
    }

    private void addAllJunit5MethodsToMap(Map<String, String> assertImports) {
        for (Junit5Asserts assertMethod : Junit5Asserts.values()) {
            assertImports.put(assertMethod.getMethodName(), AssertOrigin.JUNIT_5.getValue());
        }
    }

    private void printMethodsAfterRefactor() {
        System.out.println("\nMétodos Após refactor: \n");
        for (MethodDeclaration method: metodosTesteChamados) {
            method.getBody().ifPresent(body -> {
                for (Statement stmt : body.getStatements()) {
                    System.out.println(stmt);
                }
            });
        }
    }

    private void writeToFile(Path path, CompilationUnit compilationUnit) throws IOException {
        try (FileWriter writer = new FileWriter(path.toString())) {
            writer.write(compilationUnit.toString());
        }
    }

}
