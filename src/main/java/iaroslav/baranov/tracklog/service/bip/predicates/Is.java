package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.evaluation.EvaluationService;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import iaroslav.baranov.tracklog.unification.UnificationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
@BIP(indicator = "is/2")
public class Is implements BuildInPredicate<IsArgs> {
    private final UnificationService unificationService;
    private final EvaluationService evaluationService;

    @Override
    public BuildInPredicateExecutionResult execute(IsArgs args) {
        Term result = args.Result();
        Term expression = args.Expression();
        Term evaluatedExpression = evaluationService.evaluate(expression);

        UnificationResult unificationResult = unificationService.unifyTerms(result, evaluatedExpression);
        if (unificationResult.success()) {
            return new BuildInPredicateExecutionResult(true, unificationResult.substitution());
        } else {
            return new BuildInPredicateExecutionResult(false, new Substitution());
        }
    }
}
