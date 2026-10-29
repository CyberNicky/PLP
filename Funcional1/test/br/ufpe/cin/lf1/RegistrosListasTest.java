package br.ufpe.cin.lf1;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import lf1.plp.expressions2.expression.*;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.extension.*;
import lf1.plp.functional1.parser.Func1Parser;
import lf1.plp.functional1.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegistrosListasTest {
    private static final String PESSOA = "let record Pessoa { nome: String, idade: Int, ativa: Boolean } in ";
    private static final String MONIQUE = "Pessoa { nome: \"Monique\", idade: 23, ativa: true }";
    private static final String BRUNO = "Pessoa { nome: \"Bruno\", idade: 25, ativa: false }";

    private Programa parse(String fonte) throws Exception {
        return new Func1Parser(new StringReader(fonte)).Input();
    }
    private Valor avaliar(String fonte) throws Exception {
        Programa programa = parse(fonte);
        assertTrue(programa.checaTipo());
        return programa.executar();
    }
    private void erroTipo(String fonte, String mensagem) throws Exception {
        Programa programa = parse(fonte);
        ErroExtensao erro = assertThrows(ErroExtensao.class, programa::checaTipo);
        assertTrue(erro.getMessage(), erro.getMessage().contains(mensagem));
    }

    @Test public void criaRegistroEAcessaCamposPrimitivos() throws Exception {
        assertEquals("Monique", ((ValorString) avaliar(PESSOA + MONIQUE + ".nome")).valor());
        assertEquals(Integer.valueOf(25), ((ValorInteiro) avaliar(PESSOA + MONIQUE + ".idade + 2")).valor());
        assertFalse(((ValorBooleano) avaliar(PESSOA + "not " + MONIQUE + ".ativa")).valor());
    }

    @Test public void avaliaExpressoesNosCamposEIgnoraOrdem() throws Exception {
        assertEquals(Integer.valueOf(23), ((ValorInteiro) avaliar(PESSOA
            + "let var n = 20 in Pessoa { ativa: true, idade: n + 3, nome: \"Mo\" ++ \"nique\" }.idade")).valor());
    }

    @Test public void operaSobreListaSemAlterarOriginal() throws Exception {
        assertEquals(Integer.valueOf(48), ((ValorInteiro) avaliar(PESSOA
            + "let var pessoas = [" + MONIQUE + ", " + BRUNO + "] in "
            + "head(tail(pessoas)).idade + head(pessoas).idade")).valor());
        assertTrue(((ValorBooleano) avaliar(PESSOA + "isEmpty(tail([" + MONIQUE + "]))")).valor());
        assertTrue(((ValorBooleano) avaliar("isEmpty([])")).valor());
    }

    @Test public void listaVaziaCombinaComListaTipadaNosDoisRamos() throws Exception {
        for (String expressao : new String[] {
            "if true then [] else [" + MONIQUE + "]",
            "if false then [" + MONIQUE + "] else []"
        }) {
            assertTrue(((ValorBooleano) avaliar(PESSOA + "isEmpty(" + expressao + ")")).valor());
        }
    }

    @Test public void registrosEListasPodemPassarPorFuncaoIdentidade() throws Exception {
        assertEquals(Integer.valueOf(23), ((ValorInteiro) avaliar(PESSOA
            + "let fun identidade x = x in identidade(" + MONIQUE + ").idade")).valor());
        assertEquals(Integer.valueOf(23), ((ValorInteiro) avaliar(PESSOA
            + "let fun identidade x = x in head(identidade([" + MONIQUE + "])).idade")).valor());
    }

    @Test public void rejeitaCampoInexistenteAusenteExtraETipoErrado() throws Exception {
        erroTipo(PESSOA + MONIQUE + ".endereco", "Campo inexistente");
        erroTipo(PESSOA + "Pessoa { nome: \"M\", idade: 23 }", "Campos incorretos");
        erroTipo(PESSOA + "Pessoa { nome: \"M\", idade: 23, ativa: true, extra: 1 }", "Campos incorretos");
        erroTipo(PESSOA + "Pessoa { nome: \"M\", idade: true, ativa: true }", "Tipo incompatível");
        erroTipo(PESSOA + "Pessoa { nome: \"M\", idade: 1 + true, ativa: true }", "tipo incompatível");
        erroTipo("1.nome", "exige um registro");
    }

    @Test public void rejeitaCamposDuplicadosNaDeclaracaoENaCriacao() {
        assertThrows(ParseException.class, () -> parse("let record P { x: Int, x: Int } in 1"));
        assertThrows(ParseException.class, () -> parse("let record P { x: Int } in P { x: 1, x: 2 }"));
    }

    @Test public void rejeitaRegistrosNaoDeclaradosEForaDoEscopo() throws Exception {
        erroTipo("Pessoa { nome: \"M\" }", "não declarado");
        erroTipo("(let record P { x: Int } in P { x: 1 }.x) + P { x: 2 }.x", "não declarado");
    }

    @Test public void tiposTemIdentidadeNominal() throws Exception {
        erroTipo("let record P { x: Int } in let record Q { x: Int } in [P { x: 1 }, Q { x: 2 }]", "mesmo tipo");
        erroTipo("let record P { x: Int } in let var p = P { x: 1 } in "
            + "let record P { x: Int } in [p, P { x: 2 }]", "mesmo tipo");
    }

    @Test public void tipoEVariavelPodemTerMesmoNome() throws Exception {
        assertEquals(Integer.valueOf(7), ((ValorInteiro) avaliar(
            "let record P { x: Int } in let var P = 7 in P { x: P }.x")).valor());
    }

    @Test public void rejeitaListasHeterogeneasEOperacoesInvalidas() throws Exception {
        erroTipo("[1, true]", "mesmo tipo");
        erroTipo(PESSOA + "[" + MONIQUE + ", 1]", "mesmo tipo");
        erroTipo("[[1], [true]]", "mesmo tipo");
        erroTipo("head(1)", "exige uma lista");
        erroTipo("tail(true)", "exige uma lista");
        erroTipo("isEmpty(\"abc\")", "exige uma lista");
    }

    @Test public void operacoesEmListaVaziaTemErroExplicito() throws Exception {
        Programa headVazia = parse("head([])");
        assertTrue(headVazia.checaTipo());
        assertThrows(ErroExtensao.class, headVazia::executar);
        Programa tail = parse("tail([])");
        assertTrue(tail.checaTipo());
        assertThrows(ErroExtensao.class, tail::executar);
        Programa head = parse(PESSOA + "head(tail([" + MONIQUE + "]))");
        assertTrue(head.checaTipo());
        assertThrows(ErroExtensao.class, head::executar);
    }

    @Test public void colecoesDosValoresSaoImutaveis() throws Exception {
        ValorLista lista = (ValorLista) avaliar(PESSOA + "[" + MONIQUE + ", " + BRUNO + "]");
        assertThrows(UnsupportedOperationException.class, () -> lista.valor().clear());
        assertThrows(UnsupportedOperationException.class, () -> ((ValorRegistro) lista.head()).valor().put("idade", new ValorInteiro(99)));
        assertEquals(1, lista.tail().valor().size());
        assertEquals(2, lista.valor().size());
        assertEquals(Integer.valueOf(23), ((ValorInteiro) ((ValorRegistro) lista.head()).campo("idade")).valor());
    }

    @Test public void rejeitaAtribuicaoESintaxeInvalida() {
        assertThrows(ParseException.class, () -> parse(PESSOA + "let var p = " + MONIQUE + " in p.idade = 99"));
        assertThrows(ParseException.class, () -> parse("let record P { x: [] } in 1"));
        assertThrows(ParseException.class, () -> parse("let record P { x: [Int } in 1"));
        assertThrows(ParseException.class, () -> parse("head([], [])"));
    }

    @Test public void infereParametrosDeRegistroELista() throws Exception {
        assertEquals("Monique", ((ValorString) avaliar(PESSOA
            + "let fun nome p = p.nome in nome(" + MONIQUE + ")")).valor());
        assertTrue(((ValorBooleano) avaliar("let fun vazia xs = isEmpty(xs) in vazia([])")).valor());
    }

    @Test public void igualdadeDeRegistrosEListasUsaConteudo() throws Exception {
        assertTrue(((ValorBooleano) avaliar(PESSOA + MONIQUE + " == " + MONIQUE)).valor());
        assertFalse(((ValorBooleano) avaliar(PESSOA + MONIQUE + " == " + BRUNO)).valor());
        assertTrue(((ValorBooleano) avaliar(PESSOA + "[" + MONIQUE + "] == [" + MONIQUE + "]")).valor());
    }

    @Test public void programaPodeSerVerificadoEExecutadoMaisDeUmaVez() throws Exception {
        Programa programa = parse(PESSOA + MONIQUE + ".idade");
        for (int i = 0; i < 2; i++) {
            assertTrue(programa.checaTipo());
            assertEquals(Integer.valueOf(23), ((ValorInteiro) programa.executar()).valor());
        }
        Programa copia = new Programa(programa.getExpressao().clone());
        assertTrue(copia.checaTipo());
        assertEquals(programa.executar(), copia.executar());
    }

    @Test public void executaExemplosDocumentados() throws Exception {
        assertEquals("Monique", ((ValorString) avaliar(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/registros/pessoa.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
        assertEquals(Integer.valueOf(48), ((ValorInteiro) avaliar(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/registros/listas.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
    }
}
