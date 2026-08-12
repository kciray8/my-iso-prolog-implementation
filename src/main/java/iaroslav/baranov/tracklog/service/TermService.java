package iaroslav.baranov.tracklog.service;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.EmptyListAtom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermService {
    public Term makeRenamedCopy(Term term, ExecutionContext context) {
        Map<String, Term> renamingMap = new HashMap<>();
        for(String varName: term.collectVariableNames()){
            String newName = context.generateUniqueVariableName();
            renamingMap.put(varName, new Variable(newName));
        }
        return term.substitute(renamingMap);
    }

    public List<Term> convertToJavaList(Term list) {
        List<Term> result = new ArrayList<>();
        if (list instanceof CompoundTerm ct) {
            result.add(ct.args().get(0));
            List<Term> tailConverted = convertToJavaList(ct.args().get(1));
            result.addAll(tailConverted);
        }
        return result;
    }

    public Term convertToFunctionalList(List<Term> listElements, boolean endsWithNil) {
        if(endsWithNil) {
            Term last = new EmptyListAtom();
            for(Term term : listElements.reversed()) {
                last = new CompoundTerm(new NamedAtom("."), List.of(term, last));
            }
            return last;
        } else {
            Term last = null;
            for(Term term : listElements.reversed()) {
                if(last == null) {
                    last = term;
                } else {
                    last = new CompoundTerm(new NamedAtom("."), List.of(term, last));
                }
            }
            return last;
        }
    }

    public Term convertToFunctionalList(List<Term> listElements) {
        return convertToFunctionalList(listElements, true);
    }
}
