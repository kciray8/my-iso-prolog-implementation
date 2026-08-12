package iaroslav.baranov.tracklog.unification;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;

import java.util.ArrayList;
import java.util.List;

public class Equations {
    private List<Equation> equationsList;

    public List<Equation> getEquationsList() {
        return equationsList;
    }

    public Equations(Equation singleEquation) {
        this.equationsList = new ArrayList<Equation>();
        equationsList.add(singleEquation);
    }

    public Substitution toSubstitution() {
        Substitution substitution = new Substitution();

        for(Equation equation : equationsList) {
            Term f = equation.f();
            if(f instanceof Variable v) {
                substitution.addMapping(v.name(), equation.g());
            } else {
                throw new UnificationException("Equation left part must be a variable but got: " + f);
            }
        }

        return substitution;
    }

    public void replace(Equation equation, Equation replacement) {
        int index = equationsList.indexOf(equation);
        equationsList.remove(index);
        equationsList.add(index, replacement);
    }

    public void replace(Equation equation, List<Equation> argEquations) {
        int index = equationsList.indexOf(equation);
        equationsList.remove(index);
        equationsList.addAll(index, argEquations);
    }

    public void remove(Equation equation) {
        int index = equationsList.indexOf(equation);
        equationsList.remove(index);
    }

    public void substituteExceptPosition(String varName, Term term, int i){
        List<Equation> updatedList = new ArrayList<>();
        for(int k = 0; k < equationsList.size(); k++) {
            Equation equation = equationsList.get(k);
            if(k != i) {
                updatedList.add(equation.substitute(varName, term));
            } else {
                updatedList.add(equation);
            }
        }

        equationsList = updatedList;
    }
}
