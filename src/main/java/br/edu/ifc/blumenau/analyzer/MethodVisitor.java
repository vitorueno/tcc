package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MethodVisitor extends VoidVisitorAdapter<Path> {
    AtomicInteger numTotalAsserts;
    AtomicInteger numAssertSemDesc;
    AtomicInteger numAssertComDesc;
    AtomicInteger numAssertionRoulette;
    ArrayList<MethodDeclaration> metodosChamados;

    ArrayList<String> assertComUmParametro = new ArrayList<>();

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
        MethodDeclaration ancestor;
        try {
            ancestor = methodCallExpr.findAncestor(MethodDeclaration.class).get();
        } catch (Exception e) {
            return;
        }

        boolean isAssertionSemDescricao = qtdParametros < (assertComUmParametro.contains(nomeMetodo) ? 2 : 3);

        if (isAssertionSemDescricao) {
            numAssertSemDesc.incrementAndGet();
//            System.out.println("Assert sem descrição: " + path + " linha: " + numLinha + " Método: " + methodCallExpr);
            if (metodosChamados.contains(ancestor)) {
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
        boolean valid = false;

        for (Junit5Asserts junitAssert : Junit5Asserts.values()) {
            if (junitAssert.getMethodName().equals(nomeAssert)) {
                valid = true;
                break;
            }
        }

        for (Junit4Asserts junitAssert : Junit4Asserts.values()) {
            if (junitAssert.getMethodName().equals(nomeAssert) || valid) {
                valid = true;
                break;
            }
        }

        return valid;
    }
}
