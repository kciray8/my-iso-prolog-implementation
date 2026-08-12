package iaroslav.baranov.tracklog.ast.term;

import tools.jackson.core.io.schubfach.DoubleToDecimal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public record FloatNumberTerm(double value) implements NumericTerm {
    static DecimalFormat df = new DecimalFormat("0.0################");

    public String getPrincipalFunctor() {
        return value + "/0";
    }

    @Override
    public String toCode() {
        return df.format(value);
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
