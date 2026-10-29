package lf1.plp.functional1.extension;

/** Erro de tipo ou de execução das construções da extensão. */
public class ErroExtensao extends RuntimeException {
    private final int linha;
    private final int coluna;
    private final String contexto;

    public ErroExtensao(String mensagem) { this(mensagem, 0, 0, null); }
    private ErroExtensao(String mensagem, int linha, int coluna, String contexto) {
        super(mensagem);
        this.linha = linha;
        this.coluna = coluna;
        this.contexto = contexto;
    }
    public ErroExtensao localizar(int linha, int coluna, String contexto) {
        return new ErroExtensao(super.getMessage(), this.linha == 0 ? linha : this.linha,
            this.coluna == 0 ? coluna : this.coluna, this.contexto == null ? contexto : this.contexto);
    }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
    @Override public String getMessage() {
        return (linha == 0 ? "" : "linha " + linha + ", coluna " + coluna + ": ")
            + (contexto == null ? "" : contexto + ": ") + super.getMessage();
    }
}
