package lf1.plp.functional1.extension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;
import lf1.plp.expressions2.memory.VariavelNaoDeclaradaException;

public final class ExpRegistro extends ExpressaoExtensao {
    private final String nome;
    private final Map<String, Expressao> campos;
    // Ligação nominal resolvida no escopo da declaração da expressão.
    private TipoRegistro tipoVerificado;
    public ExpRegistro(String nome, Map<String, Expressao> campos) {
        this.nome = nome;
        this.campos = Collections.unmodifiableMap(new LinkedHashMap<>(campos));
    }
    @Override public boolean checaTipo(AmbienteCompilacao amb) {
        TipoRegistro tipo;
        try { tipo = (TipoRegistro) amb.get(TipoRegistro.chave(nome)); }
        catch (VariavelNaoDeclaradaException e) { throw new ErroExtensao("Tipo de registro não declarado: " + nome); }
        if (!tipo.campos().keySet().equals(campos.keySet()))
            throw new ErroExtensao("Campos incorretos de " + nome + ": esperados " + tipo.campos().keySet()
                + ", recebidos " + campos.keySet());
        for (Map.Entry<String, Expressao> campo : campos.entrySet()) {
            if (!campo.getValue().checaTipo(amb)) return false;
            Tipo recebido = campo.getValue().getTipo(amb);
            Tipo esperado = tipo.campos().get(campo.getKey());
            if (!esperado.eIgual(recebido)) throw ExpressaoLocalizada.localizar(campo.getValue(),
                new ErroExtensao("Tipo incompatível no campo '" + campo.getKey() + "' de " + nome
                    + ": esperado " + esperado + ", recebido " + recebido));
        }
        tipoVerificado = tipo;
        return true;
    }
    public TipoRegistro getTipo(AmbienteCompilacao amb) {
        try { return (TipoRegistro) amb.get(TipoRegistro.chave(nome)); }
        catch (VariavelNaoDeclaradaException e) { throw new ErroExtensao("Tipo de registro não declarado: " + nome); }
    }
    public ValorRegistro avaliar(AmbienteExecucao amb) {
        TipoRegistro tipo = tipoVerificado != null ? tipoVerificado
            : ((ExpDeclaracaoRegistro.Definicao) amb.get(TipoRegistro.chave(nome))).valor();
        Map<String, Valor> valores = new LinkedHashMap<>();
        campos.forEach((campo, exp) -> valores.put(campo, exp.avaliar(amb)));
        return new ValorRegistro(tipo, valores);
    }
    public ExpRegistro clone() {
        Map<String, Expressao> copia = new LinkedHashMap<>();
        campos.forEach((campo, exp) -> copia.put(campo, exp.clone()));
        ExpRegistro resultado = new ExpRegistro(nome, copia);
        resultado.tipoVerificado = tipoVerificado;
        return resultado;
    }
}
