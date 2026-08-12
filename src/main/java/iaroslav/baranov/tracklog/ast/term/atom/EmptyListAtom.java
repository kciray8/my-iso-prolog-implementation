package iaroslav.baranov.tracklog.ast.term.atom;

import iaroslav.baranov.tracklog.ast.term.Term;

import java.util.Map;

public record EmptyListAtom() implements Atom {
    public String getPrincipalFunctor() {
        return "[]/0";
    }

    @Override
    public String toCode() {
        return "[]";
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        return this;
    }
}
