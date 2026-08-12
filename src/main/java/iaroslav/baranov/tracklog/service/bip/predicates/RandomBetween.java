package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.LeftAndRight;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.UnificationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
@BIP(indicator = "random_between/3")
public class RandomBetween implements BuildInPredicate<RandomBetweenArgs> {
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult execute(RandomBetweenArgs args) {
        if(args.L() instanceof IntegerTerm lInteger) {
            if(args.U() instanceof IntegerTerm uInteger) {
                int l = lInteger.num();
                int u = uInteger.num() + 1;
                int value = ThreadLocalRandom.current().nextInt(l, u);
                IntegerTerm rInteger = new IntegerTerm(value);
                UnificationResult result = unificationService.unifyTerms(rInteger, args.R());
                if(result.success()){
                    return success(result.substitution());
                } else {
                    throw new UnificationException("Unable to unify R with integer. R = " + args.R().toCode());
                }
            } else {
                throw new WrongPredicateArgumentTypeException("U is not an integer: " + args.U());
            }
        } else {
            throw new WrongPredicateArgumentTypeException("L is not an integer: " + args.L());
        }
    }
}
