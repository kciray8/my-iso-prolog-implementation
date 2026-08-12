package iaroslav.baranov.tracklog.unification;

import iaroslav.baranov.tracklog.ast.term.Term;

public record Equation(Term f, Term g) {
    @Override
    public String toString() {
        return f + " = " + g;
    }

    public boolean contains(String v) {
        return f.contains(v) || g.contains(v);
    }

    public Equation substitute(String varName, Term term) {
        return new Equation(f.substitute(varName, term), g.substitute(varName, term));
    }
}
