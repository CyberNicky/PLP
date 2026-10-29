package lf1.plp.expressions2.memory;

import java.util.Collection;
import lf1.plp.expressions1.util.Tipo;

public interface AmbienteCompilacao extends Ambiente<Tipo> {
    /** Tipos visíveis, usados para preservar as variáveis capturadas na generalização. */
    Collection<Tipo> tiposVisiveis();
}
