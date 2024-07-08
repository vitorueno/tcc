package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    static String openSourceProjectsDir= "/home/vitor-otto/opensource";
    private static AtomicInteger totalAsserts = new AtomicInteger(0);
    private static AtomicInteger assertsSemDesc = new AtomicInteger(0);

    public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
        Path projectDir = Paths.get(openSourceProjectsDir);

        try {
            Files.walk(projectDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(Main::analyzeFile);

            System.out.println("Total asserts: " + totalAsserts);
            System.out.println("Asserts sem descrição: " + assertsSemDesc);
        } catch (Exception e) {

        }

    }

    private static void analyzeFile(Path path) {
        try (FileInputStream in = new FileInputStream(path.toFile())) {
            JavaParser parser = new JavaParser();
            CompilationUnit compilationUnit = parser.parse(in).getResult().orElse(null);

            if (compilationUnit == null) {
                return;
            }

            MethodVisitor methodVisitor = new MethodVisitor(totalAsserts, assertsSemDesc);
            compilationUnit.accept(methodVisitor, path);
        } catch (IOException e) {

        }
    }

}