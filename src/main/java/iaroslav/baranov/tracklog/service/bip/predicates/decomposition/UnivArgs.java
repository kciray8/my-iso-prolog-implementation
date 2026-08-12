package iaroslav.baranov.tracklog.service.bip.predicates.decomposition;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record UnivArgs(Term Term, Term List) implements Args {

}
