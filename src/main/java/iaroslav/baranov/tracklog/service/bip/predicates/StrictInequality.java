package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.LeftAndRight;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "\\==/2")
public class StrictInequality implements BuildInPredicate<LeftAndRight> {
    @Override
    public BuildInPredicateExecutionResult execute(LeftAndRight args) {
        if (args.Left().equals(args.Right())) {
            return failure();
        } else {
            return success();
        }
    }
}
