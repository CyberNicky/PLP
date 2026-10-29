package plp.debug.funcional1;

import java.util.List;
import lf1.plp.expressions2.expression.Expressao;
import lf1.plp.expressions2.expression.Id;
import lf1.plp.functional1.declaration.DecFuncao;
import plp.debug.core.InfoEscopo;

/** Reutiliza a inferência de Funcional1 e acrescenta os metadados de escopo. */
public class DecFuncaoDebug extends DecFuncao {
    private final InfoEscopo info;
    public DecFuncaoDebug(Id id, List<Id> args, Expressao exp, InfoEscopo info) {
        super(id, new DefFuncaoDebug(args, exp, info));
        this.info = info;
    }
    @Override public DecFuncaoDebug clone() {
        lf1.plp.functional1.util.DefFuncao copy = getFuncao().clone();
        return new DecFuncaoDebug(getId().clone(), copy.getListaId(), copy.getExp(), info);
    }
}
