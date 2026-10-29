package lf1.plp.functional1.extension;

import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.expressions2.memory.VariavelNaoDeclaradaException;

/** Referência sintática resolvida no escopo da declaração do campo. */
public final class TipoNomeado extends TipoComposto {
    private final String nome;
    private final int linha;
    private final int coluna;

    public TipoNomeado(String nome, int linha, int coluna) {
        this.nome = nome;
        this.linha = linha;
        this.coluna = coluna;
    }
    public String getNome() { return nome; }
    public boolean eValido() { return false; }
    public boolean eIgual(Tipo outro) {
        throw new ErroExtensao("Tipo de campo ainda não resolvido: " + nome).localizar(linha, coluna, null);
    }

    public static Tipo resolver(Tipo tipo, AmbienteCompilacao amb) {
        if (tipo instanceof TipoNomeado) {
            TipoNomeado ref = (TipoNomeado) tipo;
            try { return amb.get(TipoRegistro.chave(ref.nome)); }
            catch (VariavelNaoDeclaradaException e) {
                throw new ErroExtensao("Tipo de registro não declarado: " + ref.nome)
                    .localizar(ref.linha, ref.coluna, null);
            }
        }
        if (tipo instanceof TipoLista) return new TipoLista(resolver(((TipoLista) tipo).elemento(), amb));
        return tipo;
    }
}
