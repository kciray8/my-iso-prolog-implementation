package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "=/2")
public class Unifiable implements BuildInPredicate<LeftAndRight> {
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult execute(LeftAndRight args) {
        UnificationResult unificationResult = unificationService.unifyTerms(args.Left(), args.Right());
        if (unificationResult.success()) {
            return success(unificationResult.substitution());
        } else {
            return failure();
        }
    }
}
