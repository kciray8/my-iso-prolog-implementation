package iaroslav.baranov.tracklog.service.bip.predicates.type.testing;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.SingleTermArg;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "var/1")
public class Var implements BuildInPredicate<SingleTermArg> {
    @Override
    public BuildInPredicateExecutionResult execute(SingleTermArg args) {
        Term term = args.term();
        if (term instanceof Variable) {
            return success();
        } else {
            return failure();
        }
    }
}
