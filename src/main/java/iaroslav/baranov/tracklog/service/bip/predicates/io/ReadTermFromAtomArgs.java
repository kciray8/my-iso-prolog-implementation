package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record ReadTermFromAtomArgs(Term Atom, Term T, Term options) implements Args {

}
