package iaroslav.baranov.tracklog.ast.term;

import java.util.*;

// Brother Dawn
public record Variable(String name) implements Term {
    public String getPrincipalFunctor() {
        throw new IllegalCall("getPrincipalFunctor() is not allowed for Variable " + name);
    }

    @Override
    public String toCode() {
        return name;
    }

    @Override
    public boolean contains(String v) {
        return name.equals(v);
    }

    @Override
    public Term substitute(String varName, Term term) {
        if(varName.equals(name)) {
            return term;
        } else {
            return this;
        }
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        return map.getOrDefault(name, this);
    }

    @Override
    public Set<String> collectVariableNames() {
        LinkedHashSet<String> varNames = new LinkedHashSet<>();
        varNames.add(name);
        return varNames;
    }
}
