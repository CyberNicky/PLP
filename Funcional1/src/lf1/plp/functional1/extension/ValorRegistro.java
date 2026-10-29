package lf1.plp.functional1.extension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.expression.ValorConcreto;
import lf1.plp.expressions2.memory.AmbienteCompilacao;

public final class ValorRegistro extends ValorConcreto<Map<String, Valor>> {
    private final TipoRegistro tipo;
    public ValorRegistro(TipoRegistro tipo, Map<String, Valor> campos) {
        super(Collections.unmodifiableMap(new LinkedHashMap<>(campos)));
        this.tipo = tipo;
    }
    public TipoRegistro getTipo(AmbienteCompilacao amb) { return tipo; }
    public Valor campo(String nome) {
        if (!valor().containsKey(nome)) throw new ErroExtensao("Campo inexistente: " + nome);
        return valor().get(nome);
    }
    public ValorRegistro clone() { return this; }
    @Override public boolean equals(Object obj) {
        if (!(obj instanceof ValorRegistro)) return false;
        ValorRegistro outro = (ValorRegistro) obj;
        return tipo == outro.tipo && valor().equals(outro.valor());
    }
    @Override public int hashCode() { return 31 * tipo.hashCode() + valor().hashCode(); }
    @Override public boolean isEquals(ValorConcreto<Map<String, Valor>> outro) { return equals(outro); }
    public String toString() {
        return tipo.getNome() + " { " + valor().entrySet().stream()
            .map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", ")) + " }";
    }
}
