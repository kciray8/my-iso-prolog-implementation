package iaroslav.baranov.tracklog.service.bip;

import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.unification.Substitution;

public interface BuildInPredicate<T extends Args>{
    default BuildInPredicateExecutionResult execute(T args){
        throw new UnsupportedOperationException("Not implemented");
    }

    default BuildInPredicateExecutionResult executeInContext(T args, ExecutionContext context){
        return execute(args);
    }

    default BuildInPredicateExecutionResult success(Substitution substitution) {
        return new BuildInPredicateExecutionResult(true, substitution);
    }

    default BuildInPredicateExecutionResult success() {
        return success(new Substitution());
    }

    default BuildInPredicateExecutionResult failure() {
        return new BuildInPredicateExecutionResult(false, new Substitution());
    }
}
