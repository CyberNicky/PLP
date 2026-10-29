package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.functional1.util.Inferencia;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

public final class ExpCampo extends ExpressaoExtensao {
    private final Expressao alvo;
    private final String campo;
    public ExpCampo(Expressao alvo, String campo) { this.alvo = alvo; this.campo = campo; }
    @Override public boolean checaTipo(AmbienteCompilacao amb) {
        return alvo.checaTipo(amb) && Inferencia.campo(alvo.getTipo(amb), campo) != null;
    }
    public Tipo getTipo(AmbienteCompilacao amb) {
        return Inferencia.campo(alvo.getTipo(amb), campo);
    }
    public Valor avaliar(AmbienteExecucao amb) { return ((ValorRegistro) alvo.avaliar(amb)).campo(campo); }
    public ExpCampo clone() { return new ExpCampo(alvo.clone(), campo); }
}
