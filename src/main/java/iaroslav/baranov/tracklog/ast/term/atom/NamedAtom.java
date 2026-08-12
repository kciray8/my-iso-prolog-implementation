package iaroslav.baranov.tracklog.ast.term.atom;

import iaroslav.baranov.tracklog.ast.term.Term;

import java.util.Map;

public record NamedAtom(String name) implements Atom {
    public String getPrincipalFunctor() {
        return name + "/0";
    }

    @Override
    public String toCode() {
        return name;
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        return this;
    }
}
