package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "write/1")
public class Write implements BuildInPredicate<WriteArgs> {
    @Override
    public BuildInPredicateExecutionResult execute(WriteArgs args) {
        System.out.print(args.Term().toCode());
        return success();
    }
}
