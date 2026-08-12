package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.service.bip.BIP;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicate;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateExecutionResult;
import iaroslav.baranov.tracklog.unification.Substitution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@BIP(indicator = "format/2")
public class Format implements BuildInPredicate<FormatArgs> {
    @Override
    public BuildInPredicateExecutionResult execute(FormatArgs args) {
        Term format = args.Format();
        Term arguments = args.Arguments();
        String formattedString = format(format, arguments);
        System.out.print(formattedString);
        return new BuildInPredicateExecutionResult(true, new Substitution());
    }

    String format(Term format, Term arguments) {
        String formatStr = ((NamedAtom) format).name();
        List<Term> argumentsList = new ArrayList<>();
        functionalListToNormal(arguments, argumentsList);

        int argumentsCounter = 0;
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        while (pos < formatStr.length()) {
            char c = formatStr.charAt(pos);
            pos++;
            if (c != '%') {
                sb.append(c);
            } else {
                char nextC = formatStr.charAt(pos);
                pos++;
                if (nextC == 's') {
                    Term argument = argumentsList.get(argumentsCounter++);
                    List<Term> thisArgAsList = new ArrayList<>();
                    functionalListToNormal(argument, thisArgAsList);
                    for (Term arg : thisArgAsList) {
                        int code = ((IntegerTerm) arg).num();
                        sb.append((char) code);
                    }
                }
            }
        }

        return sb.toString();
    }

    void functionalListToNormal(Term list, List<Term> arguments) {
        if (list instanceof CompoundTerm ct) {
            arguments.add(ct.args().get(0));
            functionalListToNormal(ct.args().get(1), arguments);
        }
    }
}
