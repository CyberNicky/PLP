package br.ufpe.cin.lf1;

import java.io.StringReader;
import java.util.Arrays;
import lf1.plp.expressions1.util.TipoPrimitivo;
import lf1.plp.expressions2.expression.*;
import lf1.plp.expressions2.memory.*;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.declaration.DecFuncao;
import lf1.plp.functional1.extension.*;
import lf1.plp.functional1.parser.Func1Parser;
import lf1.plp.functional1.util.DefFuncao;
import org.junit.Test;
import static org.junit.Assert.*;

/** Fronteira entre as regras originais da LF1 e os novos tipos compostos. */
public class RevisaoCompatibilidadeTest {
    @Test public void registrosNaoSeConfundemComTiposPrimitivosPeloNome() throws Exception {
        String[] nomes = {"INTEIRO", "BOOLEANO", "STRING"};
        String[] valores = {"1", "true", "\"texto\""};
        for (int i = 0; i < nomes.length; i++) {
            String fonte = "let record " + nomes[i] + " { x: Int } in if true then "
                + valores[i] + " else " + nomes[i] + " { x: 1 }";
            Programa programa = new Func1Parser(new StringReader(fonte)).Input();
            try { assertFalse(fonte, programa.checaTipo()); }
            catch (ErroExtensao esperado) { /* Rejeição com diagnóstico contextual. */ }
        }
    }

    @Test public void definicaoRetornaFalseParaCorpoPrimitivoInvalido() {
        Id x = new Id("x");
        DefFuncao funcao = new DefFuncao(Arrays.asList(x), new ExpSoma(x, new ValorBooleano(true)));
        assertFalse(funcao.checaTipo(new ContextoCompilacao()));
    }

    @Test public void declaracaoRetornaFalseParaCorpoPrimitivoInvalido() {
        Id x = new Id("x");
        DecFuncao funcao = new DecFuncao(new Id("f"), Arrays.asList(x), new ExpSoma(x, new ValorBooleano(true)));
        assertFalse(funcao.checaTipo(new ContextoCompilacao()));
    }

    @Test public void declaracaoRejeitaRetornoRecursivoComTipoInfinito() {
        Id x = new Id("x");
        // f(x).campo exigiria que o retorno fosse, ao mesmo tempo, o registro
        // e o tipo de um campo interno dele, criando um ciclo de tipos.
        ExpCampo corpo = new ExpCampo(new lf1.plp.functional1.expression.Aplicacao(
            new Id("f"), Arrays.asList(x)), "campo");
        DecFuncao funcao = new DecFuncao(new Id("f"), Arrays.asList(x), corpo);
        assertFalse(funcao.checaTipo(new ContextoCompilacao()));
    }

    @Test public void erroNaExtensaoNaoDeixaParametrosNemFuncaoNoAmbiente() {
        ContextoCompilacao amb = new ContextoCompilacao();
        amb.incrementa();
        amb.map(new Id("externa"), TipoPrimitivo.INTEIRO);
        ExpOperacaoLista corpo = new ExpOperacaoLista(ExpOperacaoLista.Operacao.HEAD, new ValorInteiro(1));
        DefFuncao definicao = new DefFuncao(Arrays.asList(new Id("x")), corpo);
        assertThrows(ErroExtensao.class, () -> definicao.checaTipo(amb));
        assertEquals(1, amb.tiposVisiveis().size());
        DecFuncao declaracao = new DecFuncao(new Id("f"), Arrays.asList(new Id("x")), corpo);
        assertThrows(ErroExtensao.class, () -> declaracao.checaTipo(amb));
        assertEquals(1, amb.tiposVisiveis().size());
        assertSame(TipoPrimitivo.INTEIRO, amb.get(new Id("externa")));
    }
}
