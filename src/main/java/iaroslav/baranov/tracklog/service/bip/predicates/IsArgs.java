package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record IsArgs(Term Result, Term Expression) implements Args {

}
