package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodVisitor extends VoidVisitorAdapter<Path> {
    AtomicInteger numTotalAsserts;
    AtomicInteger numAssertSemDesc;

    ArrayList<String> assertComUmParametro = new ArrayList<>();

    public MethodVisitor(AtomicInteger numTotalAsserts, AtomicInteger numAssertSemDesc) {
        this.numTotalAsserts = numTotalAsserts;
        this.numAssertSemDesc = numAssertSemDesc;

        assertComUmParametro.add("assertTrue");
        assertComUmParametro.add("AssertFalse");
        assertComUmParametro.add("AssertNull");
        assertComUmParametro.add("AssertNotNull");
    }

    @Override
    public void visit(MethodCallExpr methodCallExpr, Path path) {
        super.visit(methodCallExpr, path);

        String nomeMetodo = methodCallExpr.getNameAsString();
        int qtdParametros = methodCallExpr.getArguments().size();
        int numLinha = methodCallExpr.getBegin().get().line;

        if (nomeMetodo.startsWith("assert")) {
            numTotalAsserts.incrementAndGet();
            if ((!assertComUmParametro.contains(nomeMetodo) && qtdParametros < 3)
                    || (assertComUmParametro.contains(nomeMetodo) && qtdParametros < 2)) {
                numAssertSemDesc.incrementAndGet();
                System.out.println("Assert sem descrição: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            }
        }
    }
}
