package iaroslav.baranov.tracklog.service.evaluation;

import iaroslav.baranov.tracklog.ast.term.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

@Service
@Slf4j
public class EvaluationService {
    public NumericTerm evaluate(Term term) {
        if(term instanceof Variable v) {
            throw new EvaluationException("Unable to evaluate expression because it contains variable: " + v.name());
        } else if(term instanceof AtomicTerm) {
            if(term instanceof IntegerTerm integerTerm) {
                return integerTerm;
            } else if(term instanceof FloatNumberTerm floatNumberTerm){
                return floatNumberTerm;
            } else {
                throw new EvaluationException("Atomic term is not numeric: " + term);
            }
        } else if(term instanceof CompoundTerm ct){
            return evaluateCompoundTerm(ct);
        }
        throw new EvaluationException("Unknown term type: " + term.getClass().getName());
    }

    boolean bothIntegers(NumericTerm a, NumericTerm b) {
        return a instanceof IntegerTerm && b instanceof IntegerTerm;
    }

    FloatNumberTerm promoteIfNeeded(NumericTerm term) {
        if(term instanceof IntegerTerm integerTerm) {
            return new FloatNumberTerm(integerTerm.num());
        } else{
            return (FloatNumberTerm) term;
        }
    }

    NumericTerm performOperation(
            Term leftTerm,
            Term rightTerm,
            BiFunction<IntegerTerm, IntegerTerm, IntegerTerm> onIntegers,
            BiFunction<FloatNumberTerm, FloatNumberTerm, FloatNumberTerm> onFloats
    ) {
        NumericTerm left =  evaluate(leftTerm);
        NumericTerm right =  evaluate(rightTerm);
        if(bothIntegers(left, right)) {
            IntegerTerm leftInt = (IntegerTerm) left;
            IntegerTerm rightInt = (IntegerTerm) right;
            return onIntegers.apply(leftInt, rightInt);
        } else {
            FloatNumberTerm leftFloat = promoteIfNeeded(left);
            FloatNumberTerm rightFloat = promoteIfNeeded(right);
            return onFloats.apply(leftFloat, rightFloat);
        }
    }


    NumericTerm performSimpleOperation(
            Term leftTerm,
            Term rightTerm,
            BiFunction<Integer, Integer, Integer> onIntegers,
            BiFunction<Double, Double, Double> onFloats
    ) {
        return performOperation(
                leftTerm,
                rightTerm,
                (left, right) -> new IntegerTerm(onIntegers.apply(left.num(),right.num())),
                (left, right) -> new FloatNumberTerm(onFloats.apply(left.value(), right.value()))
                );
    }

    private NumericTerm evaluateCompoundTerm(CompoundTerm ct) {
        String name = ct.getName();

        if(name.equals("floor")) {
            FloatNumberTerm fnt =  (FloatNumberTerm) evaluate(ct.firstArg());
            return new IntegerTerm((int) Math.floor(fnt.value()));
        }

        if(name.equals("abs")) {
            IntegerTerm value = (IntegerTerm) evaluate(ct.firstArg());
            return new IntegerTerm(Math.abs(value.num()));
        }

        Term leftTerm = ct.firstArg();
        Term rightTerm = ct.secondArg();
        if(name.equals("+")) {
            return performSimpleOperation(leftTerm, rightTerm, Integer::sum, Double::sum);
        }
        if(name.equals("-")) {
            return performSimpleOperation(leftTerm, rightTerm, (a,b) -> a - b, (a,b) -> a - b);
        }
        if(name.equals("/")) {
            NumericTerm left =  evaluate(leftTerm);
            NumericTerm right =  evaluate(rightTerm);
            FloatNumberTerm leftFloat = promoteIfNeeded(left);
            FloatNumberTerm rightFloat = promoteIfNeeded(right);
            return new FloatNumberTerm(leftFloat.value() / rightFloat.value());
        }
        if(name.equals("*")) {
            return performSimpleOperation(leftTerm, rightTerm, (a,b) -> a * b, (a,b) -> a * b);
        }

        //!!!!!! MUST EXTRACT EvaluableFunctor
        throw new EvaluationException("Unnknown compound term name: " + name);
    }
}
