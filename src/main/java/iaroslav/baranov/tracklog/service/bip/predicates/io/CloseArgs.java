package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record CloseArgs(Term Stream) implements Args {

}
