package iaroslav.baranov.tracklog.unification;

import iaroslav.baranov.tracklog.ast.term.IllegalCall;
import iaroslav.baranov.tracklog.ast.term.Stream;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;

import java.util.*;

public class Substitution {
    private Map<String, Term> map = new LinkedHashMap<>();
    private Map<String, String> inverse = new HashMap<>(); //Only for variables

    public void addMapping(String var, Term term) {
        //Transitivity Optimization
        //map: V1 -> V2, args: V2 -> T1, then we will add only V1->T1
        if(inverse.containsKey(var)) {
            String inverseVar = inverse.get(var);
            map.put(inverseVar, term);//Override the intermediate value
            inverse.remove(var);
        } else {
            map.put(var, term);
            if(term instanceof Variable v) {
                inverse.put(v.name(), var);
            }
        }
    }

    public Term get(String var) {
        if(map.containsKey(var)) {
            return map.get(var);
        } else {
            for(var entry : map.entrySet()) {
                if(entry.getValue() instanceof Variable v
                        && v.name().equals(var)) {
                    return new Variable(entry.getKey());
                }
            }
        }
        return null;
    }

    public Map<String, Term> getMap() {
        return map;
    }

    public Substitution copy(){
        Substitution copy = new Substitution();
        copy.map.putAll(this.map);

        return copy;
    }

    public Substitution compose(Substitution sigma) {
        Substitution composition = new Substitution();
        for(Map.Entry<String, Term> entry : map.entrySet()) {
            String u = entry.getKey();
            Term s = entry.getValue();
            Term sSigma = s.substitute(sigma);
            Variable uAsVariable = new Variable(u);
            if(!uAsVariable.equals(sSigma)){
                composition.addMapping(u, sSigma);
            }
        }
        for(String v: sigma.getMap().keySet()){
            Term t = sigma.get(v);
            if(!map.containsKey(v)) {
                composition.addMapping(v, t);
            }
        }

        return composition;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public void addAll(Substitution substitution) {
        for(var entry: substitution.map.entrySet()){
            addMapping(entry.getKey(), entry.getValue());
        }
    }

    public int getSize() {
         return map.size();
    }
}
