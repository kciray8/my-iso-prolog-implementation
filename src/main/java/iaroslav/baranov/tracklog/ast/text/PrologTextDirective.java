package iaroslav.baranov.tracklog.ast.text;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;

public record PrologTextDirective(Term term, PrologText text) implements PrologText{
    @Override
    public String toCode() {
        if(text instanceof PrologTextNil){
            return term.toCode() + ".";
        } else {
            return term.toCode() + ".\n" + text.toCode();
        }
    }

    public Term getDirectiveContent() {
        CompoundTerm ct = (CompoundTerm)term;
        return ct.args().get(0);
    }
}
