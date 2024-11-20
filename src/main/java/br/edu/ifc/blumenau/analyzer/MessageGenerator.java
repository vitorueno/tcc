package br.edu.ifc.blumenau.analyzer;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.jetbrains.annotations.NotNull;

public class MessageGenerator {
    public static final String DESCRICAO_ERRO_PADRAO = "Insira uma descrição de erro significativo aqui";

    @NotNull
    public static Expression criarMensagemAssertEquals(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
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

    @NotNull
    public static Expression criarMensagemAssertArrayEquals(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado arrays iguais, mas ";
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

    @NotNull
    public static Expression criarMensagemAssertFalse(Expression param1, String nomeVar1) {

        String mensagemParte1 = "Era esperado falso, mas ";
        String mensagemParte2 = String.format(" <%s> é verdadeiro ", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            result = new StringLiteralExpr(mensagemParte1 + "o valor é verdadeiro");
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertNotEquals(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado valores diferentes, mas ";
        String mensagemParte2 = String.format(" <%s> é igual a ", param1.toString());
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

    @NotNull
    public static Expression criarMensagemAssertNotNull(Expression param1, String nomeVar1) {
        String mensagemParte1 = "Era esperado não nulo, mas ";
        String mensagemParte2 = String.format(" <%s> é nulo", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            result = new StringLiteralExpr(mensagemParte1 + " o valor obtido foi nulo");
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertNotSame(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado objetos diferentes, mas ";
        String mensagemParte2 = String.format(" <%s> é igual a ", param1.toString());
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

    @NotNull
    public static Expression criarMensagemAssertNull(Expression param1, String nomeVar1) {
        String mensagemParte1 = "Era esperado nulo, mas ";
        String mensagemParte2 = String.format(" <%s> não é nulo", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            result = new StringLiteralExpr(mensagemParte1 + "o valor obtido não é nulo");
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertSame(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado objetos iguais, mas ";
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

    @NotNull
    public static Expression criarMensagemAssertThat(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado que ";
        String mensagemParte2 = String.format(" <%s> satisfizesse a condição ", param1.toString());
        String mensagemParte3 = String.format(" <%s>, mas não a satisfez", param2.toString());

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

    @NotNull
    public static Expression criarMensagemAssertThrows(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        String mensagemParte1 = "Era esperado que a exceção ";
        String mensagemParte2 = String.format(" <%s> fosse lançada, mas não foi", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            String parte2Formatada = mensagemParte2.replace("<", "").replace(">", "");
            result = new StringLiteralExpr(mensagemParte1 + parte2Formatada);
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertTrue(Expression param1, String nomeVar1) {
        String mensagemParte1 = "Era esperado verdadeiro, mas ";
        String mensagemParte2 = String.format(" <%s> é falso", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            result = new StringLiteralExpr(mensagemParte1 + "o valor obtido é falso");
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemFail() {
        String mensagemParte1 = "A execução falhou por uma causa deliberada";

        return new StringLiteralExpr(mensagemParte1);
    }

    @NotNull
    public static Expression criarMensagemAssertAll() {
        String mensagemParte1 = "Era esperado que todos os testes deste grupo passassem, mas pelo menos um falhou";

        return new StringLiteralExpr(mensagemParte1);
    }

    @NotNull
    public static Expression criarMensagemAssertDoesNotThrow(Expression param1, String nomeVar1) {
        String mensagemParte1 = "Era esperado que nenhuma exceção fosse lançada, mas ";
        String mensagemParte2 = String.format(" <%s> foi lançada", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            String parte2Formatada = mensagemParte2.replace("<", "").replace(">", "");
            result = new StringLiteralExpr(mensagemParte1 + parte2Formatada);
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertIterableEquals(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado que as listas fossem iguais, mas ";
        String mensagemParte2 = String.format(" <%s> não é igual a ", param1.toString());
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

    @NotNull
    public static Expression criarMensagemAssertLinesMatch(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado que as linhas combinassem, mas ";
        String mensagemParte2 = String.format(" <%s> não corresponde a ", param1.toString());
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

    @NotNull
    public static Expression criarMensagemAssertInstanceOf(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado que  ";
        String mensagemParte2 = String.format(" <%s> fosse uma instancia de ", param1.toString());
        String mensagemParte3 = String.format(" <%s>, mas não é", param2.toString());

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

    @NotNull
    public static Expression criarMensagemAssertThrowsExactly(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        String mensagemParte1 = "Era esperado que a exceção ";
        String mensagemParte2 = String.format(" <%s> fosse lançada, mas não foi", param1.toString());

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            String parte2Formatada = mensagemParte2.replace("<", "").replace(">", "");
            result = new StringLiteralExpr(mensagemParte1 + parte2Formatada);
        }

        return result;
    }

    @NotNull
    public static Expression criarMensagemAssertTimeout(Expression param2, Expression param1, String nomeVar1, String nomeVar2) {
        assert param2 != null;

        String mensagemParte1 = "Era esperado que o tempo de execução ";
        String mensagemParte2 = String.format(" <%s> não fosse ultrapassado, mas foi");

        Expression result = new StringLiteralExpr(mensagemParte1);

        if (!nomeVar1.isEmpty()) {
            result = new BinaryExpr(result, new NameExpr(nomeVar1), BinaryExpr.Operator.PLUS);
            result = new BinaryExpr(result, new StringLiteralExpr(mensagemParte2), BinaryExpr.Operator.PLUS);
        } else {
            String parte2Formatada = mensagemParte2.replace("<", "").replace(">", "");
            result = new StringLiteralExpr(mensagemParte1 + parte2Formatada);
        }

        return result;
    }
}
