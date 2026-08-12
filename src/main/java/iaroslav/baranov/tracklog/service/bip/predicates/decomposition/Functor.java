package iaroslav.baranov.tracklog.service.bip.predicates.decomposition;

import iaroslav.baranov.tracklog.ast.term.*;
import iaroslav.baranov.tracklog.ast.term.atom.Atom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.MemberChkArg;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "functor/3")
public class Functor implements BuildInPredicate<FunctorArgs> {
    private UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult executeInContext(FunctorArgs args, ExecutionContext context) {
        Term term = args.Term();
        Term name = args.Name();
        Term arity = args.Arity();
        //TODO refactor & extract method
        if (term instanceof CompoundTerm ct) {
            Term realName = ct.atom();
            Term realArity = new IntegerTerm(ct.arity());
            UnificationResult nameUnified = unificationService.unifyTermsOrThrow(name, realName);
            UnificationResult arityUnified = unificationService.unifyTermsOrThrow(arity, realArity);
            Substitution substitution = nameUnified.substitution();
            substitution.addAll(arityUnified.substitution());
            return success(substitution);
        } else if (term instanceof NamedAtom na) {
            Term realName = na;
            Term realArity = new IntegerTerm(0);
            UnificationResult nameUnified = unificationService.unifyTermsOrThrow(name, realName);
            UnificationResult arityUnified = unificationService.unifyTermsOrThrow(arity, realArity);
            Substitution substitution = nameUnified.substitution();
            substitution.addAll(arityUnified.substitution());
            return success(substitution);
        } else if (term instanceof IntegerTerm || term instanceof FloatNumberTerm) {
            Term realArity = new IntegerTerm(0);
            UnificationResult nameUnified = unificationService.unifyTermsOrThrow(name, term);
            UnificationResult arityUnified = unificationService.unifyTermsOrThrow(arity, realArity);
            Substitution substitution = nameUnified.substitution();
            substitution.addAll(arityUnified.substitution());
            return success(substitution);
        } else if(term instanceof Variable v) {
            List<Term> variables = new ArrayList<>();
            int num = ((IntegerTerm) arity).num();
            for(int i = 0; i < num; i++) {
                variables.add(new Variable(context.generateUniqueVariableName()));
            }
            Term constructedTerm = new CompoundTerm((Atom) name, variables);

            UnificationResult termUnified = unificationService.unifyTermsOrThrow(term, constructedTerm);
            return success(termUnified.substitution());
        } else {
            throw new RuntimeException("NOT IMPLEMENTED");
        }
    }
}
