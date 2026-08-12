package iaroslav.baranov.tracklog.service.bip.predicates.decomposition;

import iaroslav.baranov.tracklog.ast.term.*;
import iaroslav.baranov.tracklog.ast.term.atom.Atom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "=../2")
public class Univ implements BuildInPredicate<UnivArgs> {
    private UnificationService unificationService;
    private TermService termService;

    @Override
    public BuildInPredicateExecutionResult execute(UnivArgs args) {
        Term term = args.Term();
        Term list = args.List();
        //TODO refactor & extract method
        if (term instanceof CompoundTerm ct) {
            List<Term> realList = new ArrayList<>();
            realList.add(ct.atom());
            realList.addAll(ct.args());

            Term realListF = termService.convertToFunctionalList(realList);
            Substitution substitution = unificationService.unifyTermsOrThrow(list, realListF).substitution();
            return success(substitution);
        }else if (term instanceof NamedAtom na) {
            List<Term> realList = new ArrayList<>();
            realList.add(na);
            Term realListF = termService.convertToFunctionalList(realList);
            Substitution substitution = unificationService.unifyTermsOrThrow(list, realListF).substitution();
            return success(substitution);
        } else if (term instanceof IntegerTerm || term instanceof FloatNumberTerm) {
            List<Term> realList = new ArrayList<>();
            realList.add(term);
            Term realListF = termService.convertToFunctionalList(realList);
            Substitution substitution = unificationService.unifyTermsOrThrow(list, realListF).substitution();
            return success(substitution);
        } else if (term instanceof Variable v) {
            List<Term> javaList = termService.convertToJavaList(list);
            CompoundTerm compoundTerm = new CompoundTerm((Atom)javaList.get(0), javaList.subList(1, javaList.size()));
            Substitution substitution = unificationService.unifyTermsOrThrow(term, compoundTerm).substitution();
            return success(substitution);
        } else {
            throw new RuntimeException("NOT IMPLEMENTED");
        }
    }
}
