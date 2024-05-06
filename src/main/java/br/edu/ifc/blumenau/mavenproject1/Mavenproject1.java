package br.edu.ifc.blumenau.mavenproject1;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;


enum JunitVersion {
    JUNIT_4, JUNIT_5, NONE;
}

class Visitor extends VoidVisitorAdapter<Void> {
    private JunitVersion junitVersion;

    private final String MESSAGE_ASSERT_EQUALS = "Eram esperados valores iguais, mas {argumento1} é diferente de {argumento2} (linha {numLinha}).";

    public Visitor(JunitVersion junitVersion) {
        super();
        this.junitVersion = junitVersion;
    }

    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
        super.visit(methodCall, arg);
        methodCall.findAll(MethodCallExpr.class).stream()
                .filter(call -> call.getNameAsString().equals("assertEquals"))
                .forEach(this::processMethodCall);
    }

    public void processMethodCall(MethodCallExpr call) {
        // Verifica se o método não tem o argumento message
        if (call.getArguments().size() == 2) {
            NodeList<Expression> arguments = new NodeList<>();

            Expression argumento1 = call.getArgument(0);
            Expression argumento2 = call.getArgument(1);
            int numLinha = call.getRange().get().begin.line;
            String message;

            if (junitVersion == JunitVersion.NONE) {
                return;
            }

            if (junitVersion == JunitVersion.JUNIT_4) {
                message = templateString(MESSAGE_ASSERT_EQUALS,
                        "argumento1", argumento1,
                        "argumento2", argumento2,
                        "numLinha", numLinha);

                arguments.add(new StringLiteralExpr(message));
                arguments.add(call.getArgument(0));
                arguments.add(call.getArgument(1));
            } else {
                message = templateString(MESSAGE_ASSERT_EQUALS,
                        "argumento1", argumento1,
                        "argumento2", argumento2,
                        "numLinha", numLinha);

                arguments.add(call.getArgument(0));
                arguments.add(call.getArgument(1));
                arguments.add(new StringLiteralExpr(message));
            }

            call.setArguments(arguments);

            System.out.println(call.getArguments());
        }
    }

    public static String templateString(String template, Object... args) {
        for (int i = 0; i < args.length; i += 2) {
            String key = (String) args[i];
            Object value = args[i + 1];
            template = template.replace("{" + key + "}", value.toString());
        }
        return template;
    }
}


public class Mavenproject1 {

    static String rootDirectory = "/home/vitor-otto/opensource";
    //    static String folder = rootDirectory + "/jfreechart-1.5.3";
    static String folder = rootDirectory + "/jfreechart_teste";
    static String fullFilePath = folder + "/src/test/java/org/jfree/chart/ChartRenderingInfoTest.java";

    static JunitVersion junitVersion;

    public static void main(String[] args) {
        CompilationUnit comp = createParser();
        printImports(comp);

        junitVersion = getJunitVersion(comp);

        Visitor visitor = new Visitor(junitVersion);
        comp.accept(visitor, null);

        gravarAlteracoes(comp);
    }

    public static void gravarAlteracoes(CompilationUnit comp) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fullFilePath)) {
            System.out.println("escrevendo alterações");
            fileOutputStream.write(comp.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printImports(CompilationUnit comp) {
        System.out.println("------------------------------------------------");
        System.out.println("imports");
        System.out.println("------------------------------------------------");

        for (ImportDeclaration aImport : comp.getImports()) {
            System.out.println(aImport.getNameAsString());
        }

        System.out.println();
    }

    private static JunitVersion getJunitVersion(CompilationUnit comp) {
        // Verifica as importações para JUnit 4
        List<ImportDeclaration> junit4Imports = comp.findAll(ImportDeclaration.class,
                (ImportDeclaration importDeclaration) -> importDeclaration.getNameAsString().startsWith("org.junit"));

        // Verifica as importações para JUnit 5
        List<ImportDeclaration> junit5Imports = comp.findAll(ImportDeclaration.class,
                importDeclaration -> importDeclaration.getNameAsString().startsWith("org.junit.jupiter"));

        if (!junit5Imports.isEmpty()) {
            return JunitVersion.JUNIT_5;
        } else if (!junit4Imports.isEmpty()) {
            return JunitVersion.JUNIT_4;
        } else {
            return JunitVersion.NONE;
        }
    }

    public static CompilationUnit createParser() {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        final ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
        config.setSymbolResolver(symbolSolver);
        SourceRoot sourceRoot = new SourceRoot(Path.of(folder), config);

        CompilationUnit comp = sourceRoot.parse("", fullFilePath);

        return comp;
    }

}