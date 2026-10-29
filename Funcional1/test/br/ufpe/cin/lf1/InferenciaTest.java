package br.ufpe.cin.lf1;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import lf1.plp.expressions2.expression.*;
import lf1.plp.functional1.Programa;
import lf1.plp.functional1.extension.*;
import lf1.plp.functional1.parser.Func1Parser;
import org.junit.Test;
import static org.junit.Assert.*;

public class InferenciaTest {
    private static final String TIPOS = "let record Pessoa { nome: String, idade: Int } in "
        + "let record Produto { nome: String, preco: Int } in ";
    private static final String P = "Pessoa { nome: \"Monique\", idade: 23 }";
    private static final String Q = "Produto { nome: \"Livro\", preco: 40 }";
    private static final String SOMA = "let fun somarIdades pessoas = if isEmpty(pessoas) then 0 "
        + "else head(pessoas).idade + somarIdades(tail(pessoas)) in ";
    private static final String CONTAR = "let fun contar xs = if isEmpty(xs) then 0 else 1 + contar(tail(xs)) in ";

    private Programa parse(String fonte) throws Exception { return new Func1Parser(new StringReader(fonte)).Input(); }
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
        try { assertFalse("Deveria rejeitar: " + fonte, p.checaTipo()); }
        catch (ErroExtensao esperado) { assertNotNull(esperado.getMessage()); }
    }

    @Test public void somaIdadesRecursivamenteIncluindoListaVazia() throws Exception {
        inteiro(69, TIPOS + SOMA + "somarIdades([" + P + ", " + P + ", " + P + "])");
        inteiro(0, TIPOS + SOMA + "somarIdades([])");
    }

    @Test public void contarFuncionaComTiposDiferentesEmChamadasIndependentes() throws Exception {
        inteiro(3, TIPOS + CONTAR + "contar([" + P + ", " + P + "]) + contar([" + Q + "]) + contar([])");
    }

    @Test public void projecaoDeCampoAceitaRegistrosDiferentesSemConfundirListasNominais() throws Exception {
        assertEquals("MoniqueLivro", ((ValorString) avaliar(TIPOS
            + "let fun obterNome p = p.nome in obterNome(" + P + ") ++ obterNome(" + Q + ")")).valor());
        rejeita(TIPOS + "let fun obterNome p = p.nome in obterNome(head([" + P + ", " + Q + "]))");
    }

    @Test public void tipoDoCampoRetornadoEInferidoEmCadaChamada() throws Exception {
        String tipos = "let record A { valor: Int } in let record B { valor: Boolean } in ";
        inteiro(3, tipos + "let fun obter p = p.valor in if obter(B { valor: true }) then obter(A { valor: 2 }) + 1 else 0");
    }

    @Test public void rejeitaCampoAusenteETipoIncompativelNaChamada() throws Exception {
        rejeita(TIPOS + SOMA + "somarIdades([" + Q + "])");
        rejeita("let record P { idade: String } in " + SOMA + "somarIdades([P { idade: \"23\" }])");
        rejeita("let fun nome p = p.nome in nome(1)");
        rejeita(TIPOS + "let fun f p = p.nome in f([" + P + "])");
    }

    @Test public void combinaRestricoesDeVariosCampos() throws Exception {
        inteiro(24, TIPOS + "let fun f p = if p.nome == \"Monique\" then p.idade + 1 else 0 in f(" + P + ")");
        rejeita(TIPOS + "let fun f p = if p.nome == \"Livro\" then p.idade + 1 else 0 in f(" + Q + ")");
    }

    @Test public void rejeitaUsosContraditoriosDoMesmoParametroOuCampo() throws Exception {
        rejeita("let fun f p = p.idade + (if p.idade then 1 else 0) in 0");
        rejeita("let fun f p = p + p.idade in 0");
        rejeita("let fun f p = if isEmpty(p) then p.idade else 0 in 0");
        rejeita(TIPOS + "let fun f p = p.nome.idade in f(" + P + ")");
    }

    @Test public void retornoDeHeadETailPreservaTipoNominal() throws Exception {
        inteiro(23, TIPOS + "let fun primeiro xs = head(xs) in primeiro([" + P + "]).idade");
        inteiro(40, TIPOS + "let fun resto xs = tail(xs) in head(resto([" + Q + ", " + Q + "])).preco");
        rejeita(TIPOS + "let fun primeiro xs = head(xs) in primeiro([" + Q + "]).idade");
    }

    @Test public void funcoesPodemConstruirListasHomogeneasDeParametros() throws Exception {
        inteiro(23, TIPOS + "let fun par a b = [a, b] in head(par(" + P + ", " + P + ")).idade");
        rejeita(TIPOS + "let fun par a b = [a, b] in par(" + P + ", " + Q + ")");
        inteiro(1, "let fun lista a = [a] in head(lista(1))");
    }

    @Test public void parametrosRelacionadosExigemOMesmoTipoNominal() throws Exception {
        String selecionar = "let fun escolher a b = if true then a else b in ";
        inteiro(23, TIPOS + selecionar + "escolher(" + P + ", " + P + ").idade");
        rejeita(TIPOS + selecionar + "escolher(" + P + ", " + Q + ")");
        rejeita(TIPOS + "let fun iguais a b = a == b in iguais(" + P + ", " + Q + ")");
    }

    @Test public void chamadaRecursivaNaoPodeTrocarTipoDoParametro() throws Exception {
        rejeita("let fun f xs = if isEmpty(xs) then 0 else f(1) in f([])");
        rejeita(TIPOS + "let fun f xs = if isEmpty(xs) then 0 else f(head(xs)) in f([" + P + "])");
        rejeita("let fun f x = if x == 0 then 1 else f(true) in f(2)");
        rejeita("let fun f xs = if isEmpty(xs) then 0 else f(tail(xs)) ++ \"x\" in f([])");
    }

    @Test public void retornoRecursivoPropagaCamposDoRegistro() throws Exception {
        inteiro(23, TIPOS + "let fun ultimo xs = if isEmpty(tail(xs)) then head(xs) else ultimo(tail(xs)) in "
            + "ultimo([" + P + ", " + P + "]).idade");
    }

    @Test public void composicaoDeFuncoesPreservaRestricoes() throws Exception {
        inteiro(23, TIPOS + "let fun idade p = p.idade in "
            + "let fun primeiraIdade xs = idade(head(xs)) in primeiraIdade([" + P + "])");
        rejeita(TIPOS + "let fun idade p = p.idade in "
            + "let fun primeiraIdade xs = idade(head(xs)) in primeiraIdade([" + Q + "])");
    }

    @Test public void parametrosCapturadosNaoSaoGeneralizados() throws Exception {
        inteiro(24, TIPOS + "let fun f p = let fun g n = p.idade + n in g(1) in f(" + P + ")");
        rejeita(TIPOS + "let fun f p = let fun g n = p.idade + n in g(1) in f(" + Q + ")");
        rejeita("let fun f x = let fun g y = x == y in (g(1) and g(true)) in f(1)");
    }

    @Test public void generalizacaoNaoCongelaPolimorfismoPrimitivo() throws Exception {
        inteiro(3, "let fun id x = x in if id(true) then id(2) + 1 else 0");
        inteiro(3, "let fun igual a b = a == b in if igual(true, false) then 0 else (if igual(1, 1) then 3 else 0)");
    }

    @Test public void rejeitaAritmeticaComRegistrosEListasPassadosPorIdentidade() throws Exception {
        rejeita(TIPOS + "let fun id x = x in id(" + P + ") + 1");
        rejeita(TIPOS + "let fun id x = x in id([" + P + "]) + 1");
        rejeita("let fun id x = x in id(true) + 1");
    }

    @Test public void listaVaziaInfereTipoNoContextoDaFuncao() throws Exception {
        inteiro(0, "let fun f xs = if isEmpty(xs) then 0 else head(xs).idade in f([])");
        inteiro(23, TIPOS + "let fun escolher xs ys = if isEmpty(xs) then ys else xs in head(escolher([], [" + P + "])).idade");
        inteiro(0, "let fun f xs = if isEmpty(xs) then 0 else head(xs) + 1 in f([])");
    }

    @Test public void headETailVaziosFalhamSomenteQuandoExecutados() throws Exception {
        inteiro(1, "if true then 1 else head([]).idade");
        for (String fonte : new String[] { "let fun f xs = head(xs) in f([])", "let fun f xs = tail(xs) in f([])" }) {
            Programa programa = parse(fonte);
            assertTrue(programa.checaTipo());
            assertThrows(ErroExtensao.class, programa::executar);
        }
    }

    @Test public void aridadeIncorretaTemErroDeTipo() throws Exception {
        rejeita(TIPOS + "let fun f p = p.nome in f(" + P + ", " + P + ")");
        rejeita(TIPOS + "let fun f p q = p.nome ++ q.nome in f(" + P + ")");
    }

    @Test public void inferenciaNaoVazaEntreVerificacoesEClones() throws Exception {
        Programa programa = parse(TIPOS + SOMA + "somarIdades([" + P + "])");
        for (int i = 0; i < 3; i++) {
            assertTrue(programa.checaTipo());
            assertEquals(Integer.valueOf(23), ((ValorInteiro) programa.executar()).valor());
        }
        Programa copia = new Programa(programa.getExpressao().clone());
        assertTrue(copia.checaTipo());
        assertEquals(programa.executar(), copia.executar());
        Programa invalido = parse(TIPOS + SOMA + "somarIdades([" + Q + "])");
        for (int i = 0; i < 2; i++) assertThrows(ErroExtensao.class, invalido::checaTipo);
    }

    @Test public void exemplosRecursivosExecutam() throws Exception {
        inteiro(48, new String(Files.readAllBytes(java.nio.file.Paths.get("examples/registros/somar-idades.lf1")), java.nio.charset.StandardCharsets.UTF_8));
        inteiro(2, new String(Files.readAllBytes(java.nio.file.Paths.get("examples/registros/contar.lf1")), java.nio.charset.StandardCharsets.UTF_8));
        assertEquals("Monique", ((ValorString) avaliar(new String(Files.readAllBytes(java.nio.file.Paths.get("examples/registros/obter-nome.lf1")), java.nio.charset.StandardCharsets.UTF_8))).valor());
    }

    @Test public void construtorDeRegistroInfereTiposDosParametros() throws Exception {
        inteiro(23, TIPOS + "let fun criar nome idade = Pessoa { nome: nome, idade: idade } in criar(\"M\", 23).idade");
        rejeita(TIPOS + "let fun criar nome idade = Pessoa { nome: nome, idade: idade } in criar(\"M\", true)");
    }

    @Test public void recursaoComAcumuladorMantemTiposDeTodosOsArgumentos() throws Exception {
        inteiro(48, TIPOS + "let fun soma xs total = if isEmpty(xs) then total "
            + "else soma(tail(xs), total + head(xs).idade) in soma([" + P + ", " + P + "], 2)");
        rejeita(TIPOS + "let fun soma xs total = if isEmpty(xs) then total "
            + "else soma(tail(xs), total + head(xs).idade) in soma([" + P + "], true)");
    }

    @Test public void tiposRecursivosImpossiveisSaoRejeitadosSemLoopNaInferencia() throws Exception {
        rejeita("let fun f x = [f(x)] in 1");
        rejeita("let fun f x = f([x]) in 1");
    }
}
