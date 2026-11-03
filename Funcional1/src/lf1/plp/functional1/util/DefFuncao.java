package lf1.plp.functional1.util;

import java.util.ArrayList;
import java.util.List;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Id;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.functional1.extension.ErroExtensao;

public class DefFuncao {
    protected List<Id> argsId;
    protected Expressao exp;

    public DefFuncao(List<Id> argsId, Expressao exp) {
        this.argsId = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(argsId));
        this.exp = exp;
    }
    protected void aoAbrirParametros(AmbienteCompilacao amb) {}
    protected void aposVerificarCorpo(AmbienteCompilacao amb) {}
    public List<Id> getListaId() { return argsId; }
    public Expressao getExp() { return exp; }
    public int getAridade() { return argsId.size(); }

    /** Complementa a obtenção do tipo com as restrições de tipos compostos. */
    public TipoFuncao inferir(AmbienteCompilacao amb, Id nome) {
        java.util.Collection<Tipo> externo = amb.tiposVisiveis();
        TipoFuncao assinatura = criarAssinatura();
        amb.incrementa();
        try {
            if (nome != null) amb.map(nome, assinatura);
            if (!checaTipo(amb, assinatura))
                throw new ErroExtensao("Tipos incompatíveis no corpo ou retorno da função.");
        } finally { amb.restaura(); }
        assinatura.generalizar(externo);
        return assinatura;
    }

    private TipoFuncao criarAssinatura() {
        List<Tipo> params = new ArrayList<>();
        for (Id id : argsId) params.add(new TipoPolimorfico());
        return new TipoFuncao(params, new TipoPolimorfico());
    }

    public boolean checaTipo(AmbienteCompilacao amb) {
        return checaTipo(amb, criarAssinatura());
    }

    /** Fluxo da LF1: vincula os parâmetros, verifica o corpo e devolve o resultado.
     * A assinatura compartilhada mantém as restrições das chamadas recursivas. */
    public boolean checaTipo(AmbienteCompilacao amb, TipoFuncao assinatura) {
        if (assinatura.getDominio().size() != argsId.size()) return false;
        amb.incrementa();
        try {
            aoAbrirParametros(amb);
            for (int i = 0; i < argsId.size(); i++)
                amb.map(argsId.get(i), assinatura.getDominio().get(i));
            boolean resultado = exp.checaTipo(amb);
            if (resultado)
                resultado = Inferencia.unificar(assinatura.getImagem(), exp.getTipo(amb));
            if (resultado) aposVerificarCorpo(amb);
            return resultado;
        } finally { amb.restaura(); }
    }
    public Tipo getTipo(AmbienteCompilacao amb) { return inferir(amb, null); }
    public DefFuncao clone() {
        return new DefFuncao(argsId.stream().map(Id::clone).collect(java.util.stream.Collectors.toList()), exp.clone());
    }
}
