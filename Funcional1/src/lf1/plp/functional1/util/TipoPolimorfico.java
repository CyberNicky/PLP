package lf1.plp.functional1.util;

import java.util.LinkedHashMap;
import java.util.Map;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions1.util.TipoPrimitivo;

/** Variável de inferência. Instâncias pertencem a uma verificação ou chamada. */
public class TipoPolimorfico implements Tipo {
    enum Restricao { LIVRE, REGISTRO }
    Tipo vinculo;
    Restricao restricao = Restricao.LIVRE;
    final Map<String, Tipo> campos = new LinkedHashMap<>();

    public String getNome() {
        Tipo resolvido = Inferencia.resolver(this);
        if (resolvido != this) return resolvido.getNome();
        return restricao == Restricao.LIVRE ? "?" : "?registro" + campos;
    }
    public Tipo getTipoInstanciado() { return vinculo; }
    public boolean eInteiro() { return eIgual(TipoPrimitivo.INTEIRO); }
    public boolean eBooleano() { return eIgual(TipoPrimitivo.BOOLEANO); }
    public boolean eString() { return eIgual(TipoPrimitivo.STRING); }
    public boolean eIgual(Tipo outro) { return Inferencia.unificar(this, outro); }
    public boolean eValido() { return Inferencia.resolver(this) != this; }
    public Tipo inferir() { return Inferencia.resolver(this); }
    public Tipo intersecao(Tipo outro) { return eIgual(outro) ? Inferencia.resolver(this) : null; }
    public String toString() { return getNome(); }
}
