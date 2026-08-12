package iaroslav.baranov.tracklog.processor.execution.stack;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.predicates.backtracking.PredicateBacktrackingInfo;

import java.util.List;
import java.util.stream.Collectors;

public class BacktrackInformation{
    private BacktrackInformationType type;
    private List<Term> clauses;
    private PredicateBacktrackingInfo predicateBacktrackingInfo;

    public void setPredicateBacktrackingInfo(PredicateBacktrackingInfo predicateBacktrackingInfo) {
        this.predicateBacktrackingInfo = predicateBacktrackingInfo;
    }

    BacktrackInformation(
            BacktrackInformationType type,
            List<Term> clauses
    ){
        this.type = type;
        this.clauses = clauses;
    }

    BacktrackInformation(
            BacktrackInformationType type,
            PredicateBacktrackingInfo predicateBacktrackingInfo
    ){
        this.type = type;
        this.predicateBacktrackingInfo = predicateBacktrackingInfo;
    }

    public BacktrackInformationType getType() {
        return type;
    }

    public List<Term> getClauses() {
        return clauses;
    }

    public PredicateBacktrackingInfo getPredicateBacktrackingInfo() {
        return predicateBacktrackingInfo;
    }

    public BacktrackInformation(List<Term> clauses){
        this(BacktrackInformationType.UP, clauses);
    }

    public BacktrackInformation(BacktrackInformationType type){
        if(type == BacktrackInformationType.UP){
            throw new IllegalArgumentException("Backtrack information type can't be UP for this constructor");
        }
        this(type, (List<Term>)null);
    }


    public static BacktrackInformation nil(){
        return new BacktrackInformation(BacktrackInformationType.NIL);
    }

    public static BacktrackInformation ctrl(){
        return new BacktrackInformation(BacktrackInformationType.CTRL);
    }

    public static BacktrackInformation bip(){
        return new BacktrackInformation(BacktrackInformationType.BIP);
    }

    public static BacktrackInformation bip(PredicateBacktrackingInfo predicateBacktrackingInfo){
        return new BacktrackInformation(BacktrackInformationType.BIP, predicateBacktrackingInfo);
    }

    public boolean noClauses() {
        return clauses.isEmpty();
    }

    public String toCode() {
       if(type == BacktrackInformationType.UP){
           String clausesJoined = this.clauses.stream()
                   .map(Term::toCode)
                   .collect(Collectors.joining(", ", "(", ")"));

           return type + clausesJoined;
       } else {
           return type.toString();
       }
    }
}
