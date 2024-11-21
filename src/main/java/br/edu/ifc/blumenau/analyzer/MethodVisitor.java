package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodVisitor extends VoidVisitorAdapter<Path> {
    AtomicInteger numTotalAsserts;
    AtomicInteger numAssertSemDesc;
    AtomicInteger numAssertComDesc;
    AtomicInteger numAssertionRoulette;
    ArrayList<MethodDeclaration> metodosChamados;

    ArrayList<String> assertComUmParametro = new ArrayList<>();
    private MethodDeclaration ancestor;
    private Set<String> assertsJunit;

    public MethodVisitor(AtomicInteger numTotalAsserts, AtomicInteger numAssertSemDesc, AtomicInteger numAssertComDesc, AtomicInteger numAssertionRoulette, ArrayList<MethodDeclaration> metodosChamados) {
        this.numTotalAsserts = numTotalAsserts;
        this.numAssertSemDesc = numAssertSemDesc;
        this.numAssertComDesc = numAssertComDesc;
        this.numAssertionRoulette = numAssertionRoulette;
        this.metodosChamados = metodosChamados;

        assertComUmParametro.add("assertTrue");
        assertComUmParametro.add("assertFalse");
        assertComUmParametro.add("assertNull");
        assertComUmParametro.add("assertNotNull");
        assertComUmParametro.add("assertDoesNotThrow");
        assertComUmParametro.add("assertThrows");


        assertsJunit = obterConjuntoAssertsJunit();
    }

    @Override
    public void visit(MethodCallExpr methodCallExpr, Path path) {
        super.visit(methodCallExpr, path);

        String nomeMetodo = methodCallExpr.getNameAsString();
        int qtdParametros = methodCallExpr.getArguments().size();
        int numLinha = methodCallExpr.getBegin().get().line;

        boolean isValidAssert = isValidAssert(nomeMetodo);

        if (!isValidAssert) {
            return;
        }

        numTotalAsserts.incrementAndGet();
        try {
            ancestor = methodCallExpr.findAncestor(MethodDeclaration.class).get();
        } catch (Exception e) {
            return;
        }

        boolean isAssertionSemDescricao = qtdParametros < (assertComUmParametro.contains(nomeMetodo) ? 2 : 3);

        if (isAssertionSemDescricao) {
            numAssertSemDesc.incrementAndGet();
//            System.out.println("Assert sem descrição: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            if (numeroAsserts() > 1) {
                numAssertionRoulette.incrementAndGet();
//                System.out.println("AssertionRoulette: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            }
        } else {
            numAssertComDesc.incrementAndGet();
            System.out.println("Assert com mensagem: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
        }

        if (!metodosChamados.contains(ancestor)) {
            metodosChamados.add(ancestor);
        }
    }

    private boolean isValidAssert(String nomeAssert) {
        return assertsJunit.contains(nomeAssert);
    }

    private int numeroAsserts() {
        final int[] count = {0};
        ancestor.getBody().ifPresent(body -> {
            body.findAll(MethodCallExpr.class).forEach(methodCall -> {
                if (assertsJunit.contains(methodCall.getNameAsString())) {
                    count[0]++;
                }
            });
        });

        return count[0];
    }

    private static Set<String> obterConjuntoAssertsJunit() {
        Set<String> enumValues = new HashSet<>();
        for (Junit5Asserts junitAssert : Junit5Asserts.values()) {
            enumValues.add(junitAssert.getMethodName());
        }

        for (Junit4Asserts junitAssert : Junit4Asserts.values()) {
            enumValues.add(junitAssert.getMethodName());
        }

        return enumValues;
    }
}
