package br.ufpe.cin.lf1;

import java.io.StringReader;

import lf1.plp.expressions2.expression.*;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.extension.*;
import lf1.plp.functional1.parser.Func1Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListasGenericasTest {

    private Programa parse(String fonte) throws Exception {
        return new Func1Parser(new StringReader(fonte)).Input();
    }

    private Valor avaliar(String fonte) throws Exception {
        Programa p = parse(fonte);
        assertTrue(p.checaTipo());
        return p.executar();
    }

    private void inteiro(int esperado, String fonte) throws Exception {
        assertEquals(Integer.valueOf(esperado), ((ValorInteiro) avaliar(fonte)).valor());
    }

    private void rejeita(String fonte) throws Exception {
        Programa p = parse(fonte);
        try {
            assertFalse("Deveria rejeitar: " + fonte, p.checaTipo());
        } catch (ErroExtensao esperado) {
            assertNotNull(esperado.getMessage());
        }
    }

    @Test
    public void listasDosTresTiposPrimitivos() throws Exception {
        inteiro(3, "head([1, 2]) + head(tail([1, 2]))");
        assertEquals("b", ((ValorString) avaliar("head(tail([\"a\", \"b\"]))")).valor());
        assertTrue(((ValorBooleano) avaliar("head([true, false])")).valor());
    }

    @Test
    public void listasAninhadasPreservamTipoEElementos() throws Exception {
        inteiro(3, "head(head(tail([[1, 2], [3, 4]])))");
        assertTrue(((ValorBooleano) avaliar("isEmpty(head([[], [1]]))")).valor());
        inteiro(1, "head(head(tail([[], [1]])))");
        rejeita("[[1], [true]]");
        rejeita("[1, [2]]");
    }

    @Test
    public void listaVaziaRefinaTipoEmAmbosOsRamos() throws Exception {
        inteiro(2, "head(if true then [2] else [])");
        inteiro(2, "head(if false then [] else [2])");
        rejeita("[[], [1], [true]]");
    }

    @Test
    public void registroComListasDeTodosOsTiposPrimitivos() throws Exception {
        String inicio = "let record R { ns: [Int], textos: [String], flags: [Boolean] } in "
                + "let var r = R { ns: [2, 3], textos: [\"oi\"], flags: [true] } in ";
        inteiro(5, inicio + "head(r.ns) + head(tail(r.ns))");
        assertEquals("oi", ((ValorString) avaliar(inicio + "head(r.textos)")).valor());
        assertTrue(((ValorBooleano) avaliar(inicio + "head(r.flags)")).valor());
    }

    @Test
    public void registroComListaDeRegistros() throws Exception {
        inteiro(23, "let record Pessoa { idade: Int } in let record Turma { pessoas: [Pessoa] } in "
                + "let var t = Turma { pessoas: [Pessoa { idade: 23 }] } in head(t.pessoas).idade");
    }

    @Test
    public void registroComListaDeListas() throws Exception {
        inteiro(4, "let record R { matriz: [[Int]] } in "
                + "head(head(tail(R { matriz: [[1], [4, 5]] }.matriz)))");
        rejeita("let record R { matriz: [[Int]] } in R { matriz: [[true]] }");
    }

    @Test
    public void registroPodeTerOutroRegistroComoCampo() throws Exception {
        inteiro(9, "let record P { idade: Int } in let record R { pessoa: P } in "
                + "R { pessoa: P { idade: 9 } }.pessoa.idade");
        rejeita("let record P { idade: Int } in let record R { pessoa: P } in R { pessoa: 1 }");
    }

    @Test
    public void listaDeRegistrosComCamposLista() throws Exception {
        inteiro(8, "let record R { valores: [Int] } in "
                + "head(head([R { valores: [8] }]).valores)");
    }

    @Test
    public void camposListaVaziosSaoValidadosPelaDeclaracao() throws Exception {
        assertTrue(((ValorBooleano) avaliar("let record R { ns: [Int] } in isEmpty(R { ns: [] }.ns)")).valor());
        rejeita("let record R { ns: [Int] } in R { ns: [true] }");
        rejeita("let record R { ns: [Int] } in R { ns: 1 }");
        rejeita("let record R { n: Int } in R { n: [1] }");
    }

    @Test
    public void tiposNomeadosSaoResolvidosNoEscopoDaDeclaracao() throws Exception {
        rejeita("let record R { ps: [Pessoa] } in 1");
        rejeita("let record P { n: Int } in let record R { ps: [P] } in "
                + "let record P { n: Int } in R { ps: [P { n: 1 }] }");
        inteiro(1, "let record P { n: Int } in let record R { ps: [P] } in "
                + "let var p = P { n: 1 } in let record P { outro: String } in head(R { ps: [p] }.ps).n");
    }

    @Test
    public void tipoNaoDeclaradoIndicaPosicaoDaReferencia() throws Exception {
        Programa p = parse("let record R {\n  ps: [Pessoa]\n} in 1");
        ErroExtensao e = assertThrows(ErroExtensao.class, p::checaTipo);
        assertEquals(2, e.getLinha());
        assertEquals(8, e.getColuna());
        assertTrue(e.getMessage().contains("Pessoa"));
    }

    @Test
    public void somaRecursivaDeInteirosIncluiCasoVazio() throws Exception {
        String soma = "let fun soma xs = if isEmpty(xs) then 0 else head(xs) + soma(tail(xs)) in ";
        inteiro(6, soma + "soma([1, 2, 3])");
        inteiro(0, soma + "soma([])");
        rejeita(soma + "soma([true])");
    }

    @Test
    public void funcaoGenericaOperaSobreListasDeTiposDiferentes() throws Exception {
        inteiro(3, "let fun contar xs = if isEmpty(xs) then 0 else 1 + contar(tail(xs)) in "
                + "contar([1, 2]) + contar([\"a\"])");
        inteiro(2, "let fun primeiro xs = head(xs) in if primeiro([true]) then primeiro([2]) else 0");
    }

    @Test
    public void inferenciaDeCampoListaMantemRelacaoComResultado() throws Exception {
        String inicio = "let record R { xs: [Int] } in let record S { xs: [Boolean] } in "
                + "let fun primeiro r = head(r.xs) in ";
        inteiro(7, inicio + "if primeiro(S { xs: [true] }) then primeiro(R { xs: [7] }) else 0");
        rejeita(inicio + "primeiro(S { xs: [true] }) + 1");
    }

    @Test
    public void construtorInfereParametroDeTipoLista() throws Exception {
        inteiro(3, "let record R { xs: [Int] } in let fun criar xs = R { xs: xs } in head(criar([3]).xs)");
        rejeita("let record R { xs: [Int] } in let fun criar xs = R { xs: xs } in criar([true])");
    }

    @Test
    public void inferenciaPermiteAcessoACampoAninhado() throws Exception {
        inteiro(5, "let record P { idade: Int } in let record R { p: P } in "
                + "let fun idade r = r.p.idade in idade(R { p: P { idade: 5 } })");
    }

    @Test
    public void igualdadeFuncionaComListasECamposCompostos() throws Exception {
        assertTrue(((ValorBooleano) avaliar("[[1], []] == [[1], []]")).valor());
        assertTrue(((ValorBooleano) avaliar("let record R { xs: [Int] } in R { xs: [1] } == R { xs: [1] }")).valor());
        assertFalse(((ValorBooleano) avaliar("[1, 2] == [1, 3]")).valor());
        rejeita("[1] == [true]");
    }

    @Test
    public void imutabilidadeIncluiListasDentroDeRegistros() throws Exception {
        ValorRegistro r = (ValorRegistro) avaliar("let record R { xs: [Int] } in R { xs: [1, 2] }");
        ValorLista lista = (ValorLista) r.campo("xs");
        assertThrows(UnsupportedOperationException.class, () -> lista.valor().add(new ValorInteiro(3)));
        assertThrows(UnsupportedOperationException.class, () -> r.valor().put("xs", new ValorInteiro(1)));
        assertEquals(1, lista.tail().valor().size());
        assertEquals(2, lista.valor().size());
        inteiro(1, "let record R { xs: [Int] } in let var r = R { xs: [1, 2] } in "
                + "let var resto = tail(r.xs) in head(r.xs)");
    }

    @Test
    public void homogeneidadeContinuaObrigatoria() throws Exception {
        rejeita("[1, true]");
        rejeita("[\"a\", 1]");
        rejeita("let record R { x: Int } in [R { x: 1 }, 1]");
    }

    @Test(timeout = 2000)
    public void restricoesCiclicasNaoCausamLoopNaInferencia() throws Exception {
        rejeita("let fun f p = p == p.campo in 1");
        rejeita("let fun f p = p.campo == p in 1");
        rejeita("let fun f xs = head(xs) == xs in 1");
    }

    @Test
    public void checagemEClonagemPreservamTiposCompostos() throws Exception {
        Programa p = parse("let record P { x: Int } in let record R { ps: [P] } in head(R { ps: [P { x: 2 }] }.ps).x");
        for (int i = 0; i < 2; i++) {
            assertTrue(p.checaTipo());
            assertEquals(new ValorInteiro(2), p.executar());
        }
        Programa copia = new Programa(p.getExpressao().clone());
        assertTrue(copia.checaTipo());
        assertEquals(p.executar(), copia.executar());
    }

    @Test
    public void funcoesNaoSaoValoresDeListaNaFuncional1() throws Exception {
        rejeita("let fun f x = x in [f]");
    }

}
