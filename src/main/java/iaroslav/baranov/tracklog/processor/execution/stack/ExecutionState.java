package iaroslav.baranov.tracklog.processor.execution.stack;

import iaroslav.baranov.tracklog.ast.term.IllegalCall;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.unification.Substitution;

import java.util.List;

public class ExecutionState {
    private int index;
    private CurrentGoal currentGoal;
    private Substitution substitution;
    private BacktrackInformation backtrackInformation;

    public ExecutionState(
            int index,
            CurrentGoal currentGoal,
            Substitution substitution,
            BacktrackInformation backtrackInformation
    ) {
        this.index = index;
        this.currentGoal = currentGoal;
        this.substitution = substitution;
        this.backtrackInformation = backtrackInformation;
    }

    public int getChoicePoint() {
        return index - 1;
    }

    public CurrentGoal getCurrentGoal() {
        return currentGoal;
    }

    public void setCurrentGoal(CurrentGoal currentGoal) {
        this.currentGoal = currentGoal;
    }

    public void substituteInCurrentGoal(Substitution substitution) {
        currentGoal.substitute(substitution);
    }

    public int getIndex() {
        return index;
    }

    public void incrementIndex(){
        index++;
    }

    public Substitution getSubstitution() {
        return substitution;
    }

    public BacktrackInformation getBacktrackInformation() {
        return backtrackInformation;
    }

    public void setBacktrackInformation(BacktrackInformation backtrackInformation) {
        this.backtrackInformation = backtrackInformation;
    }

    public ExecutionState copyWithIncrement() {
        return new ExecutionState(
                index + 1,
                currentGoal.copy(),
                substitution.copy(),
                backtrackInformation
        );
    }

    public void replaceCurrentSubgoal(Term activator, int cutParent) {
        currentGoal.replaceCurrentSubgoal(activator, cutParent);
    }

    public void replaceCurrentSubgoal(List<Term> activators, int cutParent) {
        currentGoal.replaceCurrentSubgoal(activators, cutParent);
    }

    public void setSubstitution(Substitution substitution) {
        this.substitution = substitution;
    }

    public void removeHeadOfClauseList() {
        if(backtrackInformation.getType() != BacktrackInformationType.UP) {
            throw new IllegalCall("Trying to remove a clause when backtrack info is " + backtrackInformation.getType());
        }

        if(backtrackInformation.noClauses()) {
            throw new IllegalCall("Trying to remove a clause when no clauses are present in the clause list");
        }

        backtrackInformation.getClauses().removeFirst();
    }
}
