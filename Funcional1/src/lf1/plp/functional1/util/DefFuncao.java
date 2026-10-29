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

    /** Usa os mesmos parâmetros e retorno no corpo e na assinatura recursiva. */
    public TipoFuncao inferir(AmbienteCompilacao amb, Id nome) {
        java.util.Collection<Tipo> externo = amb.tiposVisiveis();
        List<Tipo> params = new ArrayList<>();
        for (Id id : argsId) params.add(new TipoPolimorfico());
        TipoFuncao assinatura = new TipoFuncao(params, new TipoPolimorfico());
        amb.incrementa();
        try {
            if (nome != null) amb.map(nome, assinatura);
            amb.incrementa();
            try {
                aoAbrirParametros(amb);
                for (int i = 0; i < argsId.size(); i++) amb.map(argsId.get(i), params.get(i));
                if (!exp.checaTipo(amb) || !Inferencia.unificar(assinatura.getImagem(), exp.getTipo(amb)))
                    throw new ErroExtensao("Tipos incompatíveis no corpo ou retorno da função.");
                aposVerificarCorpo(amb);
            } finally { amb.restaura(); }
        } finally { amb.restaura(); }
        assinatura.generalizar(externo);
        return assinatura;
    }
    public boolean checaTipo(AmbienteCompilacao amb) { inferir(amb, null); return true; }
    public Tipo getTipo(AmbienteCompilacao amb) { return inferir(amb, null); }
    public DefFuncao clone() {
        return new DefFuncao(argsId.stream().map(Id::clone).collect(java.util.stream.Collectors.toList()), exp.clone());
    }
}
