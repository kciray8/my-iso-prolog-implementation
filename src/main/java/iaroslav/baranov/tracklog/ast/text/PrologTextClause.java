package iaroslav.baranov.tracklog.ast.text;

import iaroslav.baranov.tracklog.ast.term.Term;

public record PrologTextClause(Term term, PrologText text)  implements PrologText{
    public String toCode(){
        if(text instanceof PrologTextNil){
            return term.toCode() + ".";
        } else {
            return term.toCode() + ".\n" + text.toCode();
        }
    }
}
