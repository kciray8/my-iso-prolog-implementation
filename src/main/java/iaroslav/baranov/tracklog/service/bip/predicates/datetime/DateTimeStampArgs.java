package iaroslav.baranov.tracklog.service.bip.predicates.datetime;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.Args;

public record DateTimeStampArgs(Term DateTime, Term TimeStamp) implements Args {

}
