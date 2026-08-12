package iaroslav.baranov.tracklog.processor.execution.stack;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.unification.Substitution;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class CurrentGoal {
    private Deque<DecoratedSubgoal> decoratedSubgoalStack;

    public CurrentGoal(Deque<DecoratedSubgoal> decoratedSubgoalStack) {
        this.decoratedSubgoalStack = decoratedSubgoalStack;
    }

    public Deque<DecoratedSubgoal> getDecoratedSubgoalStack() {
        return decoratedSubgoalStack;
    }

    public void substitute(Substitution substitution) {
        Deque<DecoratedSubgoal> currentDSS = getDecoratedSubgoalStack();
        Deque<DecoratedSubgoal> substitutedDSS =  new ArrayDeque<>();
        for(DecoratedSubgoal ds : currentDSS) {
            substitutedDSS.addLast(new DecoratedSubgoal(ds.activator().substitute(substitution), ds.cutParent()));
        }
        decoratedSubgoalStack = substitutedDSS;
    }

    public void replaceCurrentSubgoal(Term activator, int cutParent) {
        decoratedSubgoalStack.pop();
        decoratedSubgoalStack.push(new DecoratedSubgoal(activator, cutParent));
    }

    public DecoratedSubgoal getCurrentSubgoal() {
        return decoratedSubgoalStack.peek();
    }

    public int getCurrentCutParent() {
        return getCurrentSubgoal().cutParent();
    }

    public void replaceCurrentSubgoal(List<Term> activators, int cutParent) {
        decoratedSubgoalStack.pop();
        for(Term activator : activators.reversed()) {
            decoratedSubgoalStack.push(new DecoratedSubgoal(activator, cutParent));
        }
    }

    public void popCurrentDecoratedGoal() {
        getDecoratedSubgoalStack().pop();
    }

    public void pushDecoratedSubgoal(Term activator, int cutParent) {
        decoratedSubgoalStack.push(new DecoratedSubgoal(activator, cutParent));
    }

    public CurrentGoal copy() {
        return new CurrentGoal(new ArrayDeque<>(decoratedSubgoalStack));
    }
}
