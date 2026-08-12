package iaroslav.baranov.tracklog.ast.term;

import java.util.Map;

public record IntegerTerm(int num) implements NumericTerm {
    public String getPrincipalFunctor() {
        return num + "/0";
    }

    @Override
    public String toCode() {
        return Integer.toString(num);
    }

    @Override
    public boolean contains(String v) {
        return false;
    }

    @Override
    public Term substitute(String varName, Term term) {
        return this;
    }

    @Override
    public Term substitute(Map<String, Term> map) {
        return this;
    }
}
