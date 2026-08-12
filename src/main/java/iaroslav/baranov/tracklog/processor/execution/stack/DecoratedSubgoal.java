package iaroslav.baranov.tracklog.processor.execution.stack;

import iaroslav.baranov.tracklog.ast.term.Term;

public record DecoratedSubgoal(
        Term activator,
        int cutParent
) {
    public String toCode(){
        return "(" + activator.toCode() + "," + cutParent + ")";
    }
}
