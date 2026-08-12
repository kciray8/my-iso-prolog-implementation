package iaroslav.baranov.tracklog.service.bip.predicates.atomic.term.processing;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record AtomCharsArgs(Term Atom, Term List) implements Args {

}
