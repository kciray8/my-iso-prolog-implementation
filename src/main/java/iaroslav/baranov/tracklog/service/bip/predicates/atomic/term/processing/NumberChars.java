package iaroslav.baranov.tracklog.service.bip.predicates.atomic.term.processing;

import iaroslav.baranov.tracklog.ast.term.FloatNumberTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "number_chars/2")
public class NumberChars implements BuildInPredicate<NumberCharsArgs> {
    private UnificationService unificationService;
    private TermService termService;

    @Override
    public BuildInPredicateExecutionResult execute(NumberCharsArgs args) {
        Term number = args.Number();
        Term list = args.List();

        List<Term> characters = new ArrayList<>();
        if(number instanceof IntegerTerm it) {
            String numStr = String.valueOf(it.num());
            for(char c : numStr.toCharArray()) {
                String name = Character.toString(c);
                characters.add(new NamedAtom(name));
            }
        } else if(number instanceof FloatNumberTerm fnt) {
            String numStr = fnt.toCode();
            for(char c : numStr.toCharArray()) {
                String name = Character.toString(c);
                characters.add(new NamedAtom(name));
            }
        }
        Term numberAsList = termService.convertToFunctionalList(characters);

        Substitution substitution = unificationService.unifyTermsOrThrow(numberAsList,  list).substitution();
        return success(substitution);
    }
}
