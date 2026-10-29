package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;

/** Comportamento comum dos tipos de dados que não são primitivos. */
public abstract class TipoComposto implements Tipo {
    public boolean eInteiro() { return false; }
    public boolean eBooleano() { return false; }
    public boolean eString() { return false; }
    public boolean eValido() { return true; }
    public Tipo intersecao(Tipo outro) { return eIgual(outro) ? this : null; }
    public String toString() { return getNome(); }
}
