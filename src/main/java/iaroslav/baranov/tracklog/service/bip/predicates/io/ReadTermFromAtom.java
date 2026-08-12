package iaroslav.baranov.tracklog.service.bip.predicates.io;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.service.ReadTermService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.service.bip.predicates.PredicateExecutionException;
import iaroslav.baranov.tracklog.service.bip.predicates.RandomBetweenArgs;
import iaroslav.baranov.tracklog.service.bip.predicates.WrongPredicateArgumentFormatException;
import iaroslav.baranov.tracklog.service.bip.predicates.WrongPredicateArgumentTypeException;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import iaroslav.baranov.tracklog.unification.Substitution;
import iaroslav.baranov.tracklog.unification.UnificationException;
import lombok.AllArgsConstructor;
import org.springframework.expression.ExpressionParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
@BIP(indicator = "read_term_from_atom/3")
public class ReadTermFromAtom implements BuildInPredicate<ReadTermFromAtomArgs> {
    private final ReadTermService readTermService;
    private final TermParser termParser;
    private final TermService termService;
    private final UnificationService unificationService;

    @Override
    public BuildInPredicateExecutionResult execute(ReadTermFromAtomArgs args) {
        Term atom = args.Atom();
        Term term = args.T();
        Term options = args.options();

        if(atom instanceof NamedAtom na) {
            String src = na.name().trim();
            if(src.endsWith(".")) {
                String srcBody = src.substring(0, src.length()-1);
                Term termParsed = termParser.parse(srcBody);
                UnificationResult unificationResult = unificationService.unifyTerms(term, termParsed);
                if(unificationResult.success()) {
                    Substitution substitution = unificationResult.substitution();
                    List<Term> optionsList = termService.convertToJavaList(options);
                    handleOptions(optionsList, substitution, termParsed);
                    return success(substitution);
                } else {
                    throw new PredicateExecutionException("Not unifiable: " +
                            term.toCode() + " and " + termParsed.toCode());
                }
            } else {
                throw new WrongPredicateArgumentFormatException(
                        "'.' is expected at the end of named atom: " + atom.toCode());
            }
        } else {
            throw new WrongPredicateArgumentTypeException("Atom isn't a named atom: " + atom.toCode());
        }
    }

    private void handleOptions(
            List<Term> options,
            Substitution substitution,
            Term termParsed
    ) {
        for(Term option : options) {
            if(option instanceof CompoundTerm ct) {
                if(ct.getName().equals("variable_names")){
                    Term names = ct.firstArg();
                    Set<String> rawNames = termParsed.collectVariableNames();
                    Term namesAsList = wrapVariableNamesAsList(rawNames);
                    UnificationResult unificationResult = unificationService.unifyTerms(names, namesAsList);
                    if(unificationResult.success()) {
                        Substitution substitutionX = unificationResult.substitution();
                        substitution.addAll(substitutionX);
                    } else {
                        throw new PredicateExecutionException("Not unifiable: " +
                                names.toCode() + " and " + namesAsList.toCode());
                    }
                }
            }
        }
    }

    Term wrapVariableNamesAsList(Set<String> names){
        List<Term> result = new ArrayList<>();
        for(String name: names) {
            Term key = new NamedAtom(name);
            Term value = new Variable(name);
            result.add(new CompoundTerm("=", List.of(key, value)));
        }

        return termService.convertToFunctionalList(result);
    }
}
