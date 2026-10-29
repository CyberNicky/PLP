package lf1.plp.functional1.extension;

import java.util.List;
import lf1.plp.expressions2.expression.ValorConcreto;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.memory.AmbienteCompilacao;

public final class ValorLista extends ValorConcreto<List<Valor>> {
    private final TipoLista tipo;
    public ValorLista(TipoLista tipo, List<? extends Valor> valores) {
        super(java.util.Collections.unmodifiableList(new java.util.ArrayList<>(valores)));
        this.tipo = tipo;
    }
    public TipoLista getTipo(AmbienteCompilacao amb) { return tipo; }
    public Valor head() {
        if (valor().isEmpty()) throw new ErroExtensao("head não pode ser aplicado a uma lista vazia.");
        return valor().get(0);
    }
    public ValorLista tail() {
        if (valor().isEmpty()) throw new ErroExtensao("tail não pode ser aplicado a uma lista vazia.");
        return new ValorLista(tipo, valor().subList(1, valor().size()));
    }
    public ValorLista clone() { return this; }
}
