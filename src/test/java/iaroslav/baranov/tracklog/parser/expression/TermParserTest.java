package iaroslav.baranov.tracklog.parser.expression;

import iaroslav.baranov.tracklog.BaseIntegrationTest;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.parser.expression.prefix.IntegerParselet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TermParserTest extends BaseIntegrationTest {
    @Test
    void shouldParseNegativeNumbers(){
        Term term = termParser.parse("-1");
        Assertions.assertEquals(-1, ((IntegerTerm) term).num());
    }
}