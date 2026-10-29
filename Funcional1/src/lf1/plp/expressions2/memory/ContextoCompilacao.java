package lf1.plp.expressions2.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lf1.plp.expressions1.util.Tipo;
import lf1.plp.expressions2.expression.Id;

public class ContextoCompilacao extends Contexto<Tipo> implements AmbienteCompilacao {
    public Collection<Tipo> tiposVisiveis() {
        Map<Id, Tipo> visiveis = new HashMap<>();
        pilha.forEach(visiveis::putAll);
        return new ArrayList<>(visiveis.values());
    }
}
