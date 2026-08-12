package iaroslav.baranov.tracklog.ast.term;

import iaroslav.baranov.tracklog.unification.Substitution;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public interface Term {
    String getPrincipalFunctor();
    String toCode();
    boolean contains(String v);
    Term substitute(String varName, Term term);
    Term substitute(Map<String, Term> map);

    default Term substitute(Substitution substitution) {
        return substitute(substitution.getMap());
    }

    default Set<String> collectVariableNames(){
        return new LinkedHashSet<>();
    }

    default Term getHead(){
        if(!getPrincipalFunctor().equals(":-/2")) {
           throw new IllegalCall("Attemt to Term.getHead() for a term with incorrect principal functor: "
                   + getPrincipalFunctor());
        }
        CompoundTerm ct = (CompoundTerm) this;
        return ct.args().getFirst();
    }

    default Term getBody(){
        if(!getPrincipalFunctor().equals(":-/2")) {
            throw new IllegalCall("Attemt to Term.getBody() for a term with incorrect principal functor: "
                    + getPrincipalFunctor());
        }
        CompoundTerm ct = (CompoundTerm) this;
        return ct.args().get(1);
    }

    default boolean isFirstOrder() {
        return !getClass().equals(Variable.class);
    }
}
