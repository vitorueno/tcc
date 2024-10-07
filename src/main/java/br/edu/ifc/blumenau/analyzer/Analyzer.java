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

import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Analyzer {

    public static final String JUNIT_4 = "junit4";
    public static final String JUNIT_5 = "junit5";
    public static final String ASSERTJ = "assertj";
    public static final String HAMCREST = "hamcrest";
    public static final String DESCONHECIDO = "desconhecido";
    private  final AtomicInteger totalAsserts = new AtomicInteger(0);
    private  final AtomicInteger totalAssertsSemDesc = new AtomicInteger(0);
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

        assert openSourceProjectsDir != null;

        Path projectDir = Paths.get(openSourceProjectsDir);

//        combinedTypeSolver = new CombinedTypeSolver();
//        combinedTypeSolver.add(new ReflectionTypeSolver());

//        combinedTypeSolver.add(new JavaParserTypeSolver(projectDir));

//        symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
//        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        StaticJavaParser.getConfiguration().setAttributeComments(false);

        try {
            Files.walk(projectDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(this::analyzeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void analyzeFile(Path path) {
        try (FileInputStream in = new FileInputStream(path.toFile())) {

            CompilationUnit compilationUnit = StaticJavaParser.parse(in);

            if (compilationUnit == null) {
                return;
            }

            Map<String, String> assertImports = new HashMap<>();

            mapAssertImports(compilationUnit, assertImports);

            MethodVisitor methodVisitor = new MethodVisitor(totalAsserts, totalAssertsSemDesc, totalAssertionRoulette, metodosTesteChamados);
            compilationUnit.accept(methodVisitor, path);

            for (MethodDeclaration method: metodosTesteChamados) {
                method.getBody().ifPresent(body -> {
                    refactorAsserts(body, assertImports);
                });
            }

            printMethodsAfterRefactor();
            writeToFile(compilationUnit);

            System.out.println("Total asserts: " + totalAsserts);
            System.out.println("Asserts sem descrição: " + totalAssertsSemDesc);
            System.out.println("Assertion Roulette: " + metodosTesteChamados.size());

        } catch (IOException ignored) {

        }
    }

    private void refactorAsserts(BlockStmt body, Map<String, String> assertImports) {
        List<Statement> newStatements = new ArrayList<>();

        for (Statement stmt : body.getStatements()) {
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

            if (assertImports.get(nomeMetodo).equals(JUNIT_5) || assertImports.get(nomeMetodo).equals(JUNIT_4)) {
                Expression param1 = methodCall.getArgument(0);
                Expression param2 = null;

                if (qtdParametros == 2) {
                    param2 = methodCall.getArgument(1);
                }


                String mensagemAssert = getMensagemAssert(nomeMetodo, param2, param1, body, newStatements);
                MethodCallExpr newMethodCall = new MethodCallExpr(nomeMetodo);

                if (assertImports.get(nomeMetodo).equals(JUNIT_5)) {
                    newMethodCall = refactorJunit5(newMethodCall, param1, param2, mensagemAssert);
                } else {
                    newMethodCall = refactorJunit4(newMethodCall, param1, param2, mensagemAssert);
                }

                newStatements.add(new ExpressionStmt(newMethodCall));
            } else {
                System.out.println("refatoração não suportada");
            }
        }

        // Apply the new statements after the iteration is done
        for (Statement newStmt : newStatements) {
            body.addStatement(0, newStmt);  // Add new statements at the start of the body
        }
    }


    private MethodCallExpr refactorJunit4(MethodCallExpr newMethodCall, Expression param1, Expression param2, String mensagemAssert) {
        newMethodCall = newMethodCall.addArgument("\"" + mensagemAssert + "\"");
        newMethodCall = newMethodCall.addArgument(param1);
        if (param2 != null) {
            newMethodCall = newMethodCall.addArgument(param2);
        }
        return newMethodCall;
    }

    private MethodCallExpr refactorJunit5(MethodCallExpr newMethodCall, Expression param1, Expression param2, String mensagemAssert) {
        newMethodCall = newMethodCall.addArgument(param1.toString());
        if (param2 != null) {
            newMethodCall = newMethodCall.addArgument(param2.toString());
        }
        newMethodCall = newMethodCall.addArgument("\"" + mensagemAssert + "\"");
        return newMethodCall;
    }

    @NotNull
    private String getMensagemAssert(String nomeMetodo, Expression param2, Expression param1, BlockStmt body, List<Statement> newStatements) {
        String mensagemAssert = "";

//        if (param1 instanceof MethodCallExpr param1AsMethodCall) {
//            ResolvedMethodDeclaration resolvedMethod = param1AsMethodCall.resolve();
//            ResolvedType returnType = resolvedMethod.getReturnType();
//            String nomeVar = "tmp" + String.valueOf(contador++);
//            VariableDeclarationExpr newVar = new VariableDeclarationExpr((Type) returnType, nomeVar);
//            AssignExpr assignExpr = new AssignExpr(new NameExpr(nomeVar), param1, AssignExpr.Operator.ASSIGN);
//            body.addStatement(0, new ExpressionStmt(newVar));
//            body.addStatement(1, new ExpressionStmt(assignExpr));
//            System.out.println("param1 é chamada de método : " + param1AsMethodCall);
//        }

        String nomeVar = "";
        if (param2 instanceof MethodCallExpr param2AsMethodCall) {
            Type stringType = new ClassOrInterfaceType(null, "String");
            nomeVar = "result" + String.valueOf(this.contador++);
            MethodCallExpr valorVar = new MethodCallExpr("String.valueOf");
            valorVar.addArgument(param2AsMethodCall);
            VariableDeclarator newVar = new VariableDeclarator(stringType, nomeVar, valorVar);
            VariableDeclarationExpr declaration = new VariableDeclarationExpr(newVar);
            ExpressionStmt expression = new ExpressionStmt(declaration);
            newStatements.add(expression);
        }

        if (nomeMetodo.equals("assertEquals")) {
            assert param2 != null;
            mensagemAssert = String.format("Era esperado valores iguais, mas %s é diferente de %s", param1.toString(), param2.toString());

            if (!nomeVar.isEmpty()) {
                mensagemAssert += String.format(": %s", new NameExpr(nomeVar));
            }
        } else {
            mensagemAssert = "batata";
        }
        return mensagemAssert;
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
                System.out.println("Import direto: " + methodName + " de " + importAsString);
            } else if (importAsString.contains("org.junit.jupiter.api.Assertions") && importDeclaration.isAsterisk()) {
                System.out.println("importa todos os asserts do Junit 5");
                addAllJunit5MethodsToMap(assertImports);
            } else if (importAsString.contains("org.junit.Assert") && importDeclaration.isAsterisk()) {
                System.out.println("importa todos os asserts do Junit 4");
                addAllJunit4MethodsToMap(assertImports);
            } else if (importAsString.contains("org.assertj.core.api.Assertions") && importDeclaration.isAsterisk()) {
                System.out.println("importa todos os asserts do assertJ");
                addAllAssertJMethodsToMap(assertImports);
            } else if (importAsString.contains("org.hamcrest.MatcherAssert") && importDeclaration.isAsterisk()) {
                System.out.println("importa todos os asserts do hamcrest");
                addAllHamcrestMethodsToMap(assertImports);
            } else {
                System.out.println("origem do import desconhecida");
            }
        });
    }

    private void addAllHamcrestMethodsToMap(Map<String, String> assertImports) {
        assertImports.put("assertThat", HAMCREST);
    }

    private void addAllAssertJMethodsToMap(Map<String, String> assertImports) {
        assertImports.put("assertThat", ASSERTJ);
        assertImports.put("assertThatPredicate", ASSERTJ);
        assertImports.put("assertThatThrownBy", ASSERTJ);
        assertImports.put("assertThatCode", ASSERTJ);
        assertImports.put("assertThatObject", ASSERTJ);
        assertImports.put("assertWith", ASSERTJ);
        assertImports.put("assertThatExceptionOfType", ASSERTJ);
        assertImports.put("assertThatNoException", ASSERTJ);
        assertImports.put("assertThatNullPointerException", ASSERTJ);
        assertImports.put("assertThatIllegalArgumentException", ASSERTJ);
        assertImports.put("assertThatIOException", ASSERTJ);
        assertImports.put("assertThatIllegalStateException", ASSERTJ);
        assertImports.put("assertThatException", ASSERTJ);
        assertImports.put("assertThatRuntimeException", ASSERTJ);
        assertImports.put("assertThatReflectiveOperationException", ASSERTJ);
        assertImports.put("assertThatIndexOutOfBoundsException", ASSERTJ);
        assertImports.put("fail", ASSERTJ);
        assertImports.put("failBecauseExceptionWasNotThrown", ASSERTJ);
        assertImports.put("assertThatCharSequence", ASSERTJ);
        assertImports.put("assertThatIterable", ASSERTJ);
        assertImports.put("assertThatIterator", ASSERTJ);
        assertImports.put("assertThatCollection", ASSERTJ);
        assertImports.put("assertThatList", ASSERTJ);
        assertImports.put("assertThatStream", ASSERTJ);
        assertImports.put("assertThatPath", ASSERTJ);
        assertImports.put("assertThatComparable", ASSERTJ);
    }

    private void addAllJunit4MethodsToMap(Map<String, String> assertImports) {
        assertImports.put("assertArrayEquals", JUNIT_4);
        assertImports.put("assertEquals", JUNIT_4);
        assertImports.put("assertFalse", JUNIT_4);
        assertImports.put("assertNotEquals", JUNIT_4);
        assertImports.put("assertNotNull", JUNIT_4);
        assertImports.put("assertNotSame", JUNIT_4);
        assertImports.put("assertNull", JUNIT_4);
        assertImports.put("assertSame", JUNIT_4);
        assertImports.put("assertThat", JUNIT_4);
        assertImports.put("assertThrows", JUNIT_4);
        assertImports.put("assertTrue", JUNIT_4);
        assertImports.put("fail", JUNIT_4);
    }

    @NotNull
    private String getImportOrigin(String importAsString) {
        String origin;

        if (importAsString.contains("org.junit.Assert")) {
            origin = JUNIT_4;
        } else if (importAsString.contains("org.junit.jupiter.api.Assertions")) {
            origin = JUNIT_5;
        } else if (importAsString.contains("org.assertj.core.api.Assertions")) {
            origin = ASSERTJ;
        } else if (importAsString.contains("org.hamcrest.MatcherAssert")) {
            origin = HAMCREST;
        } else {
            origin = DESCONHECIDO;
        }
        return origin;
    }

    private void addAllJunit5MethodsToMap(Map<String, String> assertImports) {
        assertImports.put("assertAll", JUNIT_5);
        assertImports.put("assertArrayEquals", JUNIT_5);
        assertImports.put("assertEquals", JUNIT_5);
        assertImports.put("assertDoesNotThrow", JUNIT_5);
        assertImports.put("assertFalse", JUNIT_5);
        assertImports.put("assertIterableEquals", JUNIT_5);
        assertImports.put("assertLinesMatch", JUNIT_5);
        assertImports.put("assertNotEquals", JUNIT_5);
        assertImports.put("assertNotNull", JUNIT_5);
        assertImports.put("assertNotSame", JUNIT_5);
        assertImports.put("assertInstanceOf", JUNIT_5);
        assertImports.put("assertNull", JUNIT_5);
        assertImports.put("assertSame", JUNIT_5);
        assertImports.put("assertThrows", JUNIT_5);
        assertImports.put("assertThrowsExactly", JUNIT_5);
        assertImports.put("assertTimeout", JUNIT_5);
        assertImports.put("assertTimeoutPreemptively", JUNIT_5);
        assertImports.put("assertTrue", JUNIT_5);
        assertImports.put("fail", JUNIT_5);
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

    private void writeToFile(CompilationUnit compilationUnit) throws IOException {
        try (FileWriter writer = new FileWriter("output.java")) {
            writer.write(compilationUnit.toString());
        }
    }

}
