package iaroslav.baranov.tracklog.processor.execution.stack;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.unification.Substitution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ExecutionStack{
    private static final Logger log = LoggerFactory.getLogger(ExecutionStack.class);

    private Deque<ExecutionState> states;

    public void initialize(Term goal) {
        DecoratedSubgoal ds = new DecoratedSubgoal(goal, 0);
        Deque<DecoratedSubgoal> dsStack = new ArrayDeque<>();
        dsStack.add(ds);
        CurrentGoal goalObj = new CurrentGoal(dsStack);

        Substitution subs = new Substitution();

        BacktrackInformation bi = BacktrackInformation.nil();

        states = new ArrayDeque<>();
        states.add(new ExecutionState(1, goalObj, subs, bi));
    }

    public boolean isEmpty() {
        return states.isEmpty();
    }

    public ExecutionState currentState() {
        return states.peekFirst();
    }

    public void popCurrentState() {
        states.pop();
    }

    public DecoratedSubgoal currentDecoratedSubgoal() {
        return currentState().getCurrentGoal().getDecoratedSubgoalStack().peekFirst();
    }

    public Term currentActivator(){
        return currentDecoratedSubgoal().activator();
    }

    public ExecutionState copyOfCurrentState() {
        return states.peekFirst().copyWithIncrement();
    }

    public void updateBacktrackInfoToUp(List<Term> clauses){
        List<Term> clausesCopy = new ArrayList<>(clauses);
        currentState().setBacktrackInformation(
                new BacktrackInformation(BacktrackInformationType.UP, clausesCopy)
        );
    }

    public void updateBacktrackInfoToUp(){
        currentState().setBacktrackInformation(
                new BacktrackInformation(BacktrackInformationType.UP, new ArrayList<>())
        );
    }

    public void updateBacktrackInfoToCtrl(){
        currentState().setBacktrackInformation(BacktrackInformation.ctrl());
    }

    public void updateBacktrackInfoToBip(){
        currentState().setBacktrackInformation(BacktrackInformation.bip());
    }

    public void updateBacktrackInfoToNil(){
        currentState().setBacktrackInformation(BacktrackInformation.nil());
    }

    public void push(ExecutionState state) {
        states.push(state);
    }

    public void popCurrentDecoratedGoal() {
        CurrentGoal currentGoal = currentState().getCurrentGoal();
        currentGoal.popCurrentDecoratedGoal();
    }

    public void removeHeadOfClauseList(){
        currentState().removeHeadOfClauseList();
    }

    public void log() {
        log("");
    }

    public void log(String title) {
        log.debug("--- Execution Stack {} ---", title);
        for(ExecutionState state : states){
            int index = state.getIndex();
            Deque<DecoratedSubgoal> subgoalList = state.getCurrentGoal().getDecoratedSubgoalStack();
            List<String> subgoals = new ArrayList<>();
            for(DecoratedSubgoal subgoal : subgoalList){
                subgoals.add(subgoal.toCode());
            }
            String joinedSubgoals = String.join(" - ", subgoals);

            Substitution substitution = state.getSubstitution();
            BacktrackInformation bi = state.getBacktrackInformation();
            if(bi.getType() == BacktrackInformationType.UP){
                log.debug("{}: {}  {}", index, joinedSubgoals, substitution);
                for(Term clause: bi.getClauses()){
                    log.debug("UP {}", clause.toCode());
                }
            } else {
                log.debug("{}: {}  {} {}", index, joinedSubgoals, substitution, bi.getType());
            }
            break;
        }
    }
}
