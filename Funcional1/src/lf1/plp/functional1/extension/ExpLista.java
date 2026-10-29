package lf1.plp.functional1.extension;

import java.util.ArrayList;
import java.util.List;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.functional1.util.Inferencia;
import lf1.plp.functional1.util.TipoPolimorfico;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

public final class ExpLista extends ExpressaoExtensao {
    private final List<Expressao> elementos;
    public ExpLista(List<Expressao> elementos) { this.elementos = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(elementos)); }
    /** Verifica cada expressão e unifica os tipos dos elementos, sem avaliar valores. */
    @Override public boolean checaTipo(AmbienteCompilacao amb) {
        Tipo elemento = new TipoPolimorfico();
        for (Expressao exp : elementos) {
            if (!exp.checaTipo(amb)) return false;
            if (!Inferencia.unificar(elemento, exp.getTipo(amb)))
                throw new ErroExtensao("Todos os elementos da lista devem ter o mesmo tipo.");
        }
        return true;
    }
    public TipoLista getTipo(AmbienteCompilacao amb) {
        Tipo elemento = new TipoPolimorfico();
        for (Expressao exp : elementos) {
            if (!Inferencia.unificar(elemento, exp.getTipo(amb)))
                throw new ErroExtensao("Todos os elementos da lista devem ter o mesmo tipo.");
        }
        return new TipoLista(elemento);
    }

    public ValorLista avaliar(AmbienteExecucao amb) {
        List<Valor> valores = new ArrayList<>();
        Tipo elemento = new TipoPolimorfico();
        for (Expressao exp : elementos) {
            Valor valor = exp.avaliar(amb);
            if (!Inferencia.unificar(elemento, valor.getTipo(null)))
                throw new ErroExtensao("Todos os elementos da lista devem ter o mesmo tipo.");
            valores.add(valor);
        }
        return new ValorLista(new TipoLista(elemento), valores);
    }
    public ExpLista clone() { return new ExpLista(elementos.stream().map(Expressao::clone).collect(java.util.stream.Collectors.toList())); }
}
