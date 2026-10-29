package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.expression.ValorConcreto;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

/** Declara o tipo em um escopo próprio, sem alterar declarações simultâneas da LF1. */
public class ExpDeclaracaoRegistro extends ExpressaoExtensao {
    private final TipoRegistro tipo;
    private final Expressao corpo;
    public ExpDeclaracaoRegistro(TipoRegistro tipo, Expressao corpo) {
        this.tipo = tipo;
        this.corpo = corpo;
    }
    protected void aoAbrirEscopo(AmbienteCompilacao amb) {}
    @Override public boolean checaTipo(AmbienteCompilacao amb) {
        amb.incrementa();
        try {
            aoAbrirEscopo(amb);
            amb.map(TipoRegistro.chave(tipo.getNome()), tipo);
            tipo.resolverCampos(amb);
            return corpo.checaTipo(amb);
        } finally { amb.restaura(); }
    }
    public Tipo getTipo(AmbienteCompilacao amb) {
        amb.incrementa();
        try {
            aoAbrirEscopo(amb);
            amb.map(TipoRegistro.chave(tipo.getNome()), tipo);
            tipo.resolverCampos(amb);
            return corpo.getTipo(amb);
        } finally { amb.restaura(); }
    }
    public Valor avaliar(AmbienteExecucao amb) {
        amb.incrementa();
        try {
            amb.map(TipoRegistro.chave(tipo.getNome()), new Definicao(tipo));
            return corpo.avaliar(amb);
        } finally { amb.restaura(); }
    }
    public ExpDeclaracaoRegistro clone() { return new ExpDeclaracaoRegistro(tipo, corpo.clone()); }

    /** Metadado interno; não é um valor acessível por identificadores do programa. */
    static final class Definicao extends ValorConcreto<TipoRegistro> {
        Definicao(TipoRegistro tipo) { super(tipo); }
        public Tipo getTipo(AmbienteCompilacao amb) { return valor(); }
        public Definicao clone() { return this; }
    }
}
