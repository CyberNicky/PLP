package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Valor;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.AmbienteExecucao;

/** Preserva a posição do parser nos erros, sem alterar o tipo ou valor da expressão. */
public final class ExpressaoLocalizada implements Expressao {
    private final Expressao expressao;
    private final int linha;
    private final int coluna;
    private final String contexto;

    public ExpressaoLocalizada(Expressao expressao, int linha, int coluna, String contexto) {
        this.expressao = expressao;
        this.linha = linha;
        this.coluna = coluna;
        this.contexto = contexto;
    }
    public static ErroExtensao localizar(Expressao expressao, ErroExtensao erro) {
        return expressao instanceof ExpressaoLocalizada ? ((ExpressaoLocalizada) expressao).localizar(erro) : erro;
    }
    private ErroExtensao localizar(ErroExtensao erro) { return erro.localizar(linha, coluna, contexto); }
    public Valor avaliar(AmbienteExecucao amb) {
        try { return expressao.avaliar(amb); }
        catch (ErroExtensao erro) { throw localizar(erro); }
    }
    public boolean checaTipo(AmbienteCompilacao amb) {
        try {
            if (!expressao.checaTipo(amb)) throw new ErroExtensao("Expressão com tipo incompatível na extensão.");
            return true;
        } catch (ErroExtensao erro) { throw localizar(erro); }
    }
    public Tipo getTipo(AmbienteCompilacao amb) {
        try { return expressao.getTipo(amb); }
        catch (ErroExtensao erro) { throw localizar(erro); }
    }
    public Expressao reduzir(AmbienteExecucao amb) {
        return new ExpressaoLocalizada(expressao.reduzir(amb), linha, coluna, contexto);
    }
    public ExpressaoLocalizada clone() {
        return new ExpressaoLocalizada(expressao.clone(), linha, coluna, contexto);
    }
    public String toString() { return expressao.toString(); }
}
