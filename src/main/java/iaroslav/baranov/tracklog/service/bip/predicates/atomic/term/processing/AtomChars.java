package iaroslav.baranov.tracklog.service.bip.predicates.atomic.term.processing;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.decomposition.FunctorArgs;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "atom_chars/2")
public class AtomChars implements BuildInPredicate<AtomCharsArgs> {
    private UnificationService unificationService;
    private TermService termService;

    @Override
    public BuildInPredicateExecutionResult execute(AtomCharsArgs args) {
        Term atom = args.Atom();
        Term list = args.List();
        List<Term> listOfCharacters = termService.convertToJavaList(list);
        StringBuilder sb = new StringBuilder();
        for (Term term : listOfCharacters) {
            if(term instanceof NamedAtom na) {
                sb.append(na.name());
            }
        }
        Term realAtom = new NamedAtom(sb.toString());
        Substitution substitution = unificationService.unifyTermsOrThrow(atom, realAtom).substitution();
        return success(substitution);
    }
}
