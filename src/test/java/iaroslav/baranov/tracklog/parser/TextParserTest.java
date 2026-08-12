package iaroslav.baranov.tracklog.parser;

import iaroslav.baranov.tracklog.BaseIntegrationTest;
import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ast.text.PrologTextClause;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TextParserTest extends BaseIntegrationTest {
    @Test
    void shouldParsePrologText(){
        Lexer lexer = new Lexer();
        String line1 = "my_last(H, [H]).\n";
        String line2 = "my_last(L, [_|T]) :- my_last(L, T).";
        String code = line1 + line2;
        List<Token> tokens = lexer.tokenize(code);
        PrologText text = textParser.parse(tokens);

        PrologTextClause firstClause = (PrologTextClause)text;
        PrologTextClause secondClause = (PrologTextClause)firstClause.text();
        CompoundTerm ct = (CompoundTerm) secondClause.term();
        NamedAtom na =  (NamedAtom) ct.atom();
        Assertions.assertEquals(":-", na.name());

        String codeGotBack = firstClause.toCode();
        Assertions.assertEquals(code, codeGotBack);
    }


    @Test
    void shouldParseNegationAsFailure(){
        List<Token> tokens = lexer.tokenize("a :- \\+ b, c.");
        PrologText text = textParser.parse(tokens);
        Assertions.assertEquals("a :- \\+(b), c.", text.toCode());
    }
}