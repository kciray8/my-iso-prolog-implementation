package iaroslav.baranov.tracklog.service.bip.predicates.arithmetic;

import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.TwoExpressions;
import iaroslav.baranov.tracklog.service.evaluation.EvaluationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@BIP(indicator = "</2")
public class Less implements BuildInPredicate<TwoExpressions> {
    private final EvaluationService evaluationService;

    @Override
    public BuildInPredicateExecutionResult execute(TwoExpressions args) {
        int value1 = ((IntegerTerm)evaluationService.evaluate(args.Expr1())).num();
        int value2 = ((IntegerTerm)evaluationService.evaluate(args.Expr2())).num();

        if (value1 < value2) {
            return success();
        } else {
            return failure();
        }
    }
}
