package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record OpenArgs(Term Source_sink, Term Mode, Term Stream, Term Options) implements Args {

}
