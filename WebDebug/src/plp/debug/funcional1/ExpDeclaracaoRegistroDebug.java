package plp.debug.funcional1;

import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.functional1.extension.ExpDeclaracaoRegistro;
import lf1.plp.functional1.extension.TipoRegistro;
import plp.debug.core.InfoEscopo;
import plp.debug.core.ScopeAware;

public final class ExpDeclaracaoRegistroDebug extends ExpDeclaracaoRegistro {
    private final TipoRegistro tipo;
    private final Expressao corpo;
    private final InfoEscopo info;
    public ExpDeclaracaoRegistroDebug(TipoRegistro tipo, Expressao corpo, InfoEscopo info) {
        super(tipo, corpo); this.tipo = tipo; this.corpo = corpo; this.info = info;
    }
    @Override protected void aoAbrirEscopo(AmbienteCompilacao amb) {
        if (amb instanceof ScopeAware) ((ScopeAware) amb).registraEscopo(info);
    }
    @Override public ExpDeclaracaoRegistroDebug clone() {
        return new ExpDeclaracaoRegistroDebug(tipo, corpo.clone(), info);
    }
}
