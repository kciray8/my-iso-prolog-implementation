package iaroslav.baranov.tracklog.service.unification;

import iaroslav.baranov.tracklog.ast.term.AtomicTerm;
import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.unification.Equation;
import iaroslav.baranov.tracklog.unification.Equations;
import iaroslav.baranov.tracklog.unification.UnificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnificationService {
    private static final Logger log = LoggerFactory.getLogger(UnificationService.class);

    //The Herbrand algorithm
    public boolean unify(Equations equations) {
        outerLoop:
        while (true) {
            List<Equation> equationsList = equations.getEquationsList();
            //log.info("Equations list: " + equationsList);

            for (int i = 0; i < equationsList.size(); i++) {
                Equation equation = equationsList.get(i);
                Term f = equation.f();
                Term g = equation.g();
                if(log.isDebugEnabled()){
                    log.debug("UNIFICATION ATTEMPT {} = {}", f.toCode() , g.toCode());
                }

                if (f instanceof AtomicTerm && g instanceof AtomicTerm) {
                    if (!f.equals(g)) {
                        return false;
                    } else {
                        equations.remove(equation);
                        continue outerLoop;
                    }
                }
                if ((f instanceof AtomicTerm && g instanceof CompoundTerm) ||
                        (g instanceof AtomicTerm && f instanceof CompoundTerm)) {
                    return false;
                }

                if (f instanceof Variable fV && g instanceof Variable gV) {
                    if (fV.equals(gV)) {
                        equations.remove(equation);
                        continue outerLoop;
                    }
                }

                if (f instanceof CompoundTerm fCompoundTerm
                        && g instanceof CompoundTerm gCompoundTerm) {
                    if (!fCompoundTerm.getPrincipalFunctor().equals(gCompoundTerm.getPrincipalFunctor())) {
                        return false;
                    }
                    if (fCompoundTerm.arity() != gCompoundTerm.arity()) {
                        return false;
                    }

                    List<Equation> argEquations = new ArrayList<>();
                    for (int j = 0; j < fCompoundTerm.arity(); j++) {
                        Term fArg = fCompoundTerm.args().get(j);
                        Term gArg = gCompoundTerm.args().get(j);
                        argEquations.add(new Equation(fArg, gArg));
                    }
                    equations.replace(equation, argEquations);
                    continue outerLoop;
                }

                //t = X
                if (g instanceof Variable && !(f instanceof Variable)) {
                    equations.replace(equation, new Equation(g, f));
                    continue outerLoop;
                }

                //X = t
                if (f instanceof Variable(String varName)) {
                    if (g.contains(varName)) {
                        //positive occurs-check
                        return false;
                    } else {
                        if (occursInSomeOtherEquation(varName, equationsList, i)) {
                            equations.substituteExceptPosition(varName, g, i);
                            continue outerLoop;
                        }
                    }
                }
            }
            return true;
        }
    }

    boolean occursInSomeOtherEquation(String varName, List<Equation> equationsList, int i) {
        for (int k = 0; k < equationsList.size(); k++) {
            if (k != i) {
                Equation otherEquation = equationsList.get(k);
                if (otherEquation.contains(varName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public UnificationResult unifyTerms(Term f, Term g) {
        Equation equation = new Equation(f, g);
        Equations equations = new Equations(equation);
        boolean unifiable = unify(equations);
        if (unifiable) {
            return new UnificationResult(equations.toSubstitution());
        } else {
            return new UnificationResult();
        }
    }

    public UnificationResult unifyTermsOrThrow(Term f, Term g) {
        UnificationResult unificationResult = unifyTerms(f, g);
        if (unificationResult.success()) {
            return unificationResult;
        } else {
            throw new UnificationException("Not unifiable: " + f.toCode() + " and " + g.toCode());
        }
    }
}
