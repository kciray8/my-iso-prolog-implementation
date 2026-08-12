package iaroslav.baranov.tracklog.ast.term.atom;

import iaroslav.baranov.tracklog.ast.term.AtomicTerm;
import iaroslav.baranov.tracklog.ast.term.Term;

public interface Atom extends AtomicTerm{

    @Override
    default boolean contains(String v) {
        return false;
    }

    @Override
    default Term substitute(String varName, Term term) {
        return this;
    }
}
