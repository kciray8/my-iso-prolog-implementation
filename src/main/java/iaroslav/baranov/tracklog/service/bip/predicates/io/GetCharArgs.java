package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record GetCharArgs(Term S_or_a, Term Char) implements Args {

}
