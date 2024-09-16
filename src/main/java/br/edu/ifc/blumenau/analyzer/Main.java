package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import io.github.cdimascio.dotenv.Dotenv;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final AtomicInteger totalAsserts = new AtomicInteger(0);
    private static final AtomicInteger totalAssertsSemDesc = new AtomicInteger(0);
    private static final AtomicInteger totalAssertionRoulette = new AtomicInteger(0);
    private static final ArrayList<MethodDeclaration> metodosTesteChamados = new ArrayList<>();
    private static ArrayList<String> assertComUmParametro = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
        assertComUmParametro.add("assertTrue");
        assertComUmParametro.add("assertFalse");
        assertComUmParametro.add("assertNull");
        assertComUmParametro.add("assertNotNull");

        Dotenv dotenv = Dotenv.load();

        String openSourceProjectsDir = dotenv.get("PROJECTS_DIR");

        assert openSourceProjectsDir != null;

        Path projectDir = Paths.get(openSourceProjectsDir);

        try {
            Files.walk(projectDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(Main::analyzeFile);
        } catch (Exception ignored) {

        }
    }

    private static void analyzeFile(Path path) {


        try (FileInputStream in = new FileInputStream(path.toFile())) {
            JavaParser parser = new JavaParser();
            parser.getParserConfiguration().setAttributeComments(false);
            CompilationUnit compilationUnit = parser.parse(in).getResult().orElse(null);

            if (compilationUnit == null) {
                return;
            }

            MethodVisitor methodVisitor = new MethodVisitor(totalAsserts, totalAssertsSemDesc, totalAssertionRoulette, metodosTesteChamados);
            compilationUnit.accept(methodVisitor, path);

            System.out.println("Total asserts: " + totalAsserts);
            System.out.println("Asserts sem descrição: " + totalAssertsSemDesc);
            System.out.println("Assertion Roulette: " + metodosTesteChamados.size());

            for (MethodDeclaration method: metodosTesteChamados) {
                method.getBody().ifPresent(body -> {
                    for (Statement stmt : body.getStatements()) {
                        ExpressionStmt exprStmt = stmt.asExpressionStmt();

                        if (exprStmt.getExpression() instanceof MethodCallExpr methodCall) {

                            if (methodCall.getNameAsString().startsWith("assert")) {
                                int qtdParametros = methodCall.getArguments().size();
                                String nomeMetodo = methodCall.getName().asString();
                                boolean isAssertionSemDescricao = qtdParametros < (assertComUmParametro.contains(nomeMetodo) ? 2 : 3);

                                if (isAssertionSemDescricao) {
                                    String param1 = methodCall.getArgument(0).toString();
                                    String param2 = "";

                                    MethodCallExpr newMethodCall = new MethodCallExpr(nomeMetodo).addArgument(param1);
                                    if (qtdParametros == 2) {
                                        param2 = methodCall.getArgument(1).toString();
                                        newMethodCall = newMethodCall.addArgument(param2);
                                    }

                                    String mensagemAssert = "";

                                    if (nomeMetodo.equals("assertEquals")) {
                                        mensagemAssert = String.format("Era esperado valores iguais, mas %s é diferente de %s", param1, param2);
                                    } else {
                                        mensagemAssert = "batata";
                                    }

                                    newMethodCall = newMethodCall.addArgument("\"" +  mensagemAssert + "\"");

                                    stmt.replace(new ExpressionStmt(newMethodCall));
                                }
                            }
                        }
                    }
                });
            }

            for (MethodDeclaration method: metodosTesteChamados) {
                method.getBody().ifPresent(body -> {
                    for (Statement stmt : body.getStatements()) {
                        System.out.println(stmt);
                    }
                });
            }

            try (FileWriter writer = new FileWriter("output.java")) {
                writer.write(compilationUnit.toString());
            }

        } catch (IOException ignored) {

        }


    }

}
