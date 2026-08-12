package iaroslav.baranov.tracklog.service;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermFlatteningService {
    //TODO rewrite into list + queue clear combo
    public List<Term> flatten(Term term){
        Map<String, Integer> varMap = new HashMap<>();
        List<Term> result = new ArrayList<>();
        result.add(term);
        int i = 0;
        do{
            Term t = result.get(i);
            if(t instanceof CompoundTerm ct){
                List<Term> flattenedArgs = new ArrayList<>();
                for(Term arg: ct.args()){
                    if(arg instanceof Variable v) {
                        if (varMap.containsKey(v.name())) {
                            flattenedArgs.add(new Variable("" + varMap.get(v.name())));
                        } else {
                            int varIndex = result.size();
                            varMap.put(v.name(), varIndex);
                            flattenedArgs.add(new Variable("" + varIndex));
                            result.add(arg);
                        }
                    } else if (arg instanceof CompoundTerm argCt) {
                        int argIndex = result.size();
                        result.add(arg);
                        flattenedArgs.add(new Variable("" + argIndex));
                    } else {
                        flattenedArgs.add(arg);
                    }
                }
                result.set(i, new CompoundTerm(ct.atom(), flattenedArgs));
            }

            i++;
        }while (i < result.size());

        return result;
    }
}
