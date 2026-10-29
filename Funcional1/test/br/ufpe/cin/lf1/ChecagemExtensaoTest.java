package br.ufpe.cin.lf1;

import java.util.*;
import lf1.plp.expressions1.util.*;
import lf1.plp.expressions2.expression.*;
import lf1.plp.expressions2.memory.*;
import lf1.plp.functional1.extension.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class ChecagemExtensaoTest {
    private final Expressao invalida = new Expressao() {
        public boolean checaTipo(AmbienteCompilacao amb) { return false; }
        public Tipo getTipo(AmbienteCompilacao amb) { return TipoPrimitivo.INTEIRO; }
        public Valor avaliar(AmbienteExecucao amb) { throw new AssertionError("Checagem não deve executar"); }
        public Expressao reduzir(AmbienteExecucao amb) { return this; }
        public Expressao clone() { return this; }
    };
    @Test public void tipoInteiroNaoSubstituiValidacaoDaExpressao() {
        assertFalse(new ExpLista(Arrays.asList(invalida)).checaTipo(new ContextoCompilacao()));
    }
    @Test public void campoDeRegistroPropagaFalhaDoChecaTipo() {
        AmbienteCompilacao amb = new ContextoCompilacao();
        amb.incrementa();
        amb.map(TipoRegistro.chave("P"), new TipoRegistro("P", Collections.singletonMap("x", TipoPrimitivo.INTEIRO)));
        assertFalse(new ExpRegistro("P", Collections.singletonMap("x", invalida)).checaTipo(amb));
    }
    @Test public void acessosEOperacoesPropagamFalhaDoArgumento() {
        assertFalse(new ExpCampo(invalida, "x").checaTipo(new ContextoCompilacao()));
        assertFalse(new ExpOperacaoLista(ExpOperacaoLista.Operacao.HEAD, invalida).checaTipo(new ContextoCompilacao()));
    }
    @Test public void checagemNaoExecutaHeadDaListaVazia() {
        assertTrue(new ExpOperacaoLista(ExpOperacaoLista.Operacao.HEAD, new ExpLista(Collections.emptyList())).checaTipo(new ContextoCompilacao()));
    }
}
