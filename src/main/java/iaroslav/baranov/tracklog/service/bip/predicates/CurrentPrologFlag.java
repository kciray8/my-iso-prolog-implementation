package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "current_prolog_flag/2")
public class CurrentPrologFlag implements BuildInPredicate<CurrentPrologFlagArgs> {
    UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult executeInContext(CurrentPrologFlagArgs args, ExecutionContext context) {
        NamedAtom key = (NamedAtom) args.Key();
        Term value = args.Value();


        if(!context.hasFlag(key)) {
            return failure();
        }

        Term currentValue = context.getFlag(key);
        UnificationResult result = unificationService.unifyTermsOrThrow(value, currentValue);

        if(result.success()) {
            return success();
        } else {
            return failure();
        }
    }
}
