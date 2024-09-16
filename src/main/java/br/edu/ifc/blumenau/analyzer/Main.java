package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import io.github.cdimascio.dotenv.Dotenv;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
        Dotenv dotenv = Dotenv.load();

        String openSourceProjectsDir = dotenv.get("PROJECTS_DIR");

        assert openSourceProjectsDir != null;

        Path projectDir = Paths.get(openSourceProjectsDir);

        try {

            Files.walk(projectDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(Main::analyzeFile);

            System.out.println("Total asserts: " + totalAsserts);
            System.out.println("Asserts sem descrição: " + totalAssertsSemDesc);
            System.out.println("Assertion Roulette: " + metodosTesteChamados.size());
        } catch (Exception ignored) {

        }

        for (MethodDeclaration metodosTesteChamado : metodosTesteChamados) {
            System.out.println(metodosTesteChamado.getBody().get().getStatements().get(0).toString());

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
        } catch (IOException ignored) {

        }
    }

}
