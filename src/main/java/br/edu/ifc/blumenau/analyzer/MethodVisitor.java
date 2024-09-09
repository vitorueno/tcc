package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodVisitor extends VoidVisitorAdapter<Path> {
    AtomicInteger numTotalAsserts;
    AtomicInteger numAssertSemDesc;
    AtomicInteger numAssertionRoulette;
    ArrayList<String> metodosChamados;

    ArrayList<String> assertComUmParametro = new ArrayList<>();

    public MethodVisitor(AtomicInteger numTotalAsserts, AtomicInteger numAssertSemDesc, AtomicInteger numAssertionRoulette, ArrayList<String> metodosChamados) {
        this.numTotalAsserts = numTotalAsserts;
        this.numAssertSemDesc = numAssertSemDesc;
        this.numAssertionRoulette = numAssertionRoulette;
        this.metodosChamados = metodosChamados;

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

        boolean isAssert = nomeMetodo.startsWith("assert");
        if (!isAssert) {
            return;
        }

        numTotalAsserts.incrementAndGet();
        String parentMethodName = String.valueOf(methodCallExpr.findAncestor(MethodDeclaration.class).get().getName());

        boolean isAssertionSemDescricao = qtdParametros < (assertComUmParametro.contains(nomeMetodo) ? 2 : 3);

        if (isAssertionSemDescricao) {
            numAssertSemDesc.incrementAndGet();
            System.out.println("Assert sem descrição: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            if (metodosChamados.contains(parentMethodName)) {
                numAssertionRoulette.incrementAndGet();
                System.out.println("AssertionRoulette: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            }
        }

        metodosChamados.add(parentMethodName);
    }
}

