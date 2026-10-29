package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.functional1.util.Inferencia;
import lf1.plp.functional1.util.TipoPolimorfico;

/** Lista homogênea de qualquer tipo de dado, compartilhado com head/tail. */
public final class TipoLista extends TipoComposto {
    private final Tipo elemento;
    public TipoLista(Tipo elemento) {
        this.elemento = elemento == null ? new TipoPolimorfico() : elemento;
    }
    public Tipo elemento() { return Inferencia.resolver(elemento); }
    public String getNome() { return "[" + elemento().getNome() + "]"; }
    public boolean eIgual(Tipo outro) { return Inferencia.unificar(this, outro); }
    public Tipo intersecao(Tipo outro) { return eIgual(outro) ? this : null; }
}
