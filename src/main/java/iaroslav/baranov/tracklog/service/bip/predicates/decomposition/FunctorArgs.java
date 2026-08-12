package iaroslav.baranov.tracklog.service.bip.predicates.decomposition;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record FunctorArgs(Term Term, Term Name, Term Arity) implements Args {

}
