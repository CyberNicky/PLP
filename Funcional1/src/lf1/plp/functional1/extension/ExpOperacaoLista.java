package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.functional1.util.Inferencia;
import lf1.plp.expressions1.util.TipoPrimitivo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.expression.ValorBooleano;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

public final class ExpOperacaoLista extends ExpressaoExtensao {
    public enum Operacao { HEAD, TAIL, IS_EMPTY }
    private final Operacao operacao;
    private final Expressao argumento;
    public ExpOperacaoLista(Operacao operacao, Expressao argumento) {
        this.operacao = operacao;
        this.argumento = argumento;
    }
    @Override public boolean checaTipo(AmbienteCompilacao amb) {
        return argumento.checaTipo(amb) && Inferencia.lista(argumento.getTipo(amb)) != null;
    }
    public Tipo getTipo(AmbienteCompilacao amb) {
        TipoLista lista = Inferencia.lista(argumento.getTipo(amb));
        switch (operacao) {
            case IS_EMPTY: return TipoPrimitivo.BOOLEANO;
            case TAIL: return lista;
            case HEAD: return lista.elemento();
            default: throw new IllegalStateException("Operação desconhecida");
        }
    }
    public Valor avaliar(AmbienteExecucao amb) {
        ValorLista lista = (ValorLista) argumento.avaliar(amb);
        switch (operacao) {
            case HEAD: return lista.head();
            case TAIL: return lista.tail();
            case IS_EMPTY: return new ValorBooleano(lista.valor().isEmpty());
            default: throw new IllegalStateException("Operação desconhecida");
        }
    }
    public ExpOperacaoLista clone() { return new ExpOperacaoLista(operacao, argumento.clone()); }
}
