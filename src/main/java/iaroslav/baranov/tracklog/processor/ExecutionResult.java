package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.processor.execution.stack.ExecutionStack;
import iaroslav.baranov.tracklog.unification.Substitution;

public record ExecutionResult(
        boolean success,
        Substitution substitution,
        ExecutionContext executionContext,
        ExecutionStack executionStack
) {
    ExecutionResult(
            Substitution substitution,
            ExecutionContext executionContext,
            ExecutionStack executionStack) {
        this(true, substitution, executionContext, executionStack);
    }
    ExecutionResult(){
        this(false, null, null, null);
    }
}
