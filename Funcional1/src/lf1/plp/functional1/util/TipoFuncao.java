package lf1.plp.functional1.util;

import java.util.*;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.memory.AmbienteCompilacao;
import lf1.plp.functional1.extension.ErroExtensao;

/** Assinatura monomórfica durante a recursão; esquema instanciado nas demais chamadas. */
public class TipoFuncao implements Tipo {
    private final List<Tipo> dominio;
    private final Tipo imagem;
    private Set<TipoPolimorfico> quantificadas = Collections.emptySet();

    public TipoFuncao(List<Tipo> dominio, Tipo imagem) {
        this.dominio = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(dominio));
        this.imagem = imagem;
    }
    public List<Tipo> getDominio() { return dominio; }
    public Tipo getImagem() { return imagem; }
    Set<TipoPolimorfico> quantificadas() { return quantificadas; }

    public void generalizar(Collection<Tipo> ambienteExterno) {
        Set<TipoPolimorfico> livres = new HashSet<>();
        dominio.forEach(t -> livres.addAll(Inferencia.livres(t)));
        livres.addAll(Inferencia.livres(imagem));
        ambienteExterno.forEach(t -> livres.removeAll(Inferencia.livres(t)));
        quantificadas = Collections.unmodifiableSet(new HashSet<>(livres));
    }

    private TipoFuncao instanciar() {
        Map<TipoPolimorfico, TipoPolimorfico> copias = new IdentityHashMap<>();
        List<Tipo> params = dominio.stream().map(t -> Inferencia.copiar(t, quantificadas, copias)).collect(java.util.stream.Collectors.toList());
        return new TipoFuncao(params, Inferencia.copiar(imagem, quantificadas, copias));
    }

    public String getNome() { return dominio + " -> " + imagem; }
    public String toString() { return getNome(); }
    public boolean eBooleano() { return false; }
    public boolean eInteiro() { return false; }
    public boolean eString() { return false; }
    public boolean eValido() { return true; }
    public boolean eIgual(Tipo outro) { return Inferencia.unificar(this, outro); }
    public Tipo intersecao(Tipo outro) { return eIgual(outro) ? this : null; }

    private Tipo aplicar(AmbienteCompilacao amb, List<? extends Expressao> argumentos) {
        if (dominio.size() != argumentos.size()) throw new ErroExtensao("Quantidade incorreta de argumentos da função.");
        TipoFuncao chamada = instanciar();
        for (int i = 0; i < argumentos.size(); i++) {
            Expressao argumento = argumentos.get(i);
            if (!argumento.checaTipo(amb)) throw new ErroExtensao("Argumento com tipo inválido.");
            Tipo real = argumento.getTipo(amb);
            if (!Inferencia.unificar(chamada.dominio.get(i), real))
                throw new ErroExtensao("Tipo incompatível no argumento " + (i + 1)
                    + ": esperado " + chamada.dominio.get(i) + ", recebido " + real);
        }
        return Inferencia.resolver(chamada.imagem);
    }

    public boolean checaTipo(AmbienteCompilacao amb, List<? extends Expressao> argumentos) {
        aplicar(amb, argumentos);
        return true;
    }
    public Tipo getTipo(AmbienteCompilacao amb, List<? extends Expressao> argumentos) {
        return aplicar(amb, argumentos);
    }
}
