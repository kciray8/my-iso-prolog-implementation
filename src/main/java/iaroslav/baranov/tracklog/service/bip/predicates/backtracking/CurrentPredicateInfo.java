package iaroslav.baranov.tracklog.service.bip.predicates.backtracking;

import iaroslav.baranov.tracklog.ast.term.Term;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class CurrentPredicateInfo implements PredicateBacktrackingInfo {
    @Getter
    private List<Term> collectedPredicateNames; //A/N
}
