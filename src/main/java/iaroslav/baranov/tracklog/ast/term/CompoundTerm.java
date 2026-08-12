package iaroslav.baranov.tracklog.ast.term;

import iaroslav.baranov.tracklog.ast.term.atom.Atom;
import iaroslav.baranov.tracklog.ast.term.atom.EmptyCurlyBracketsAtom;
import iaroslav.baranov.tracklog.ast.term.atom.EmptyListAtom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;

import java.util.*;

// Brother Day
public record CompoundTerm(Atom atom, List<Term> args) implements Term {
    public CompoundTerm {
        if (args.isEmpty()) {
            throw new IllegalArgumentException("According to grammar, it is impossible " +
                    "for a compound term to have zero arguments");
        }
        args = Collections.unmodifiableList(args);
    }

    public CompoundTerm(String name, List<Term> args){
        this(new NamedAtom(name), args);
    }

    public int arity() {
        return args.size();
    }

    public int getPrincipalFunctorArity() {
        return arity();
    }

    public String getPrincipalFunctorIdentifier(){
        switch(atom){
            case NamedAtom na:
                return na.name();
            case EmptyListAtom eal:
                 return "[]";
            case EmptyCurlyBracketsAtom eba:
                return "{}";
            default:
                throw new IllegalStateException("Unknown atom " + atom);
        }
    }

    public String getName(){
        if(atom instanceof NamedAtom na){
            return na.name();
        } else {
            throw new IllegalCall("Atom is not named atom: " + atom.toCode());
        }
    }

    public Term firstArg() {
        if(args.isEmpty()) {
            throw new IllegalCall("No arguments");
        }
        return args.get(0);
    }

    public Term secondArg() {
        if(args.size() <= 1) {
            throw new IllegalCall("Second argument isn't available for " + this.toCode());
        }
        return args.get(1);
    }

    public String getPrincipalFunctor() {
        return getPrincipalFunctorIdentifier() + "/"  + getPrincipalFunctorArity();
    }

    void collectListElements(Term term, List<String> collector) {
        if(term instanceof CompoundTerm ct){
            if(ct.arity() == 2){
                if(ct.atom instanceof NamedAtom na){
                    if(na.name().equals(".")){
                        Term head = ct.args.get(0);
                        Term tail = ct.args.get(1);
                        collector.add(head.toCode());
                        collectListElements(tail, collector);
                    }
                }
            }
        } else {
            collector.add(term.toCode());
        }
    }

    String formatInfixOperator(String left, String op, String right) {
        if(op.equals(",")){
            return left + ", " + right;
        }

        return left + " " + op + " " + right;
    }

    @Override
    public String toCode() {
        if(atom instanceof NamedAtom(String name)) {
            if(arity() == 1 && isPrefixOperator(name)){
                return name + " " + args.get(0).toCode();
            } else if(arity() == 2 && isInfixOperator(name)) {
                return formatInfixOperator(args.get(0).toCode(), name, args.get(1).toCode());
            } else if(arity() == 2 && name.equals(".")) {
                return toCodeForList();
            } else {
                List<String> argsCode = new ArrayList<>();
                for(Term arg: args) {
                    argsCode.add(arg.toCode());
                }
                return name + "(" + String.join(", ", argsCode) + ")";
            }
        }
        return "NOT IMPLEMENTED";
    }

    private String toCodeForList() {
        List<String> listElements = new ArrayList<>();
        collectListElements(this, listElements);

        String str = "[";
        for (int i = 0; i < listElements.size() - 1; i++) {
            if(i > 0) {
                str += ", ";
            }
            str += listElements.get(i);
        }
        String lastElement = listElements.get(listElements.size() - 1);
        if(!lastElement.equals("[]")) {
            str += "|" + lastElement;
        }
        str += "]";

        return str;
    }

    @Override
    public boolean contains(String v) {
        for(Term arg: args){
            if(arg.contains(v)) {
                return true;
            }
        }

        return false;
    }

    boolean isInfixOperator(String operator) {
        if(operator.equals("+")
                || operator.equals("-")
                || operator.equals("*")
                || operator.equals("/")
                || operator.equals(":-")
                || operator.equals(";")
                || operator.equals(",")) {
            return true;
        } else {
            return false;
        }
    }

    boolean isPrefixOperator(String operator) {
        if(operator.equals(":-")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Term substitute(String varName, Term term) {
        List<Term> newArgs = new ArrayList<>();

        for(Term arg: args){
            newArgs.add(arg.substitute(varName, term));
        }

        return new CompoundTerm(atom, newArgs);
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        List<Term> newArgs = new ArrayList<>();

        for(Term arg: args){
            newArgs.add(arg.substitute(map));
        }

        return new CompoundTerm(atom, newArgs);
    }

    @Override
    public Set<String> collectVariableNames() {
        Set<String> names = new LinkedHashSet<>();

        for(Term arg: args){
            Set<String> argNames = arg.collectVariableNames();
            for(String argName: argNames){
                if(!names.contains(argName)){
                    names.add(argName);
                }
            }
        }

        return names;
    }
}
