package lf1.plp.functional1.extension;

import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

public abstract class ExpressaoExtensao implements Expressao {
    public abstract boolean checaTipo(AmbienteCompilacao amb);
    public Expressao reduzir(AmbienteExecucao amb) { return clone(); }
    public abstract Expressao clone();
}
