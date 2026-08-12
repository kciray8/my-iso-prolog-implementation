package iaroslav.baranov.tracklog.unification;

import iaroslav.baranov.tracklog.BaseIntegrationTest;
import iaroslav.baranov.tracklog.BaseTest;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class UnifiableServiceTest extends BaseIntegrationTest {
    UnificationService unificationService = new UnificationService();

    @Test
    public void testUnificationOfNumbers() {
        Term three = parse("3");
        Term threeDub = parse("3");
        Term four = parse("4");
        UnificationResult result = unificationService.unifyTerms(three, threeDub);
        UnificationResult result2 = unificationService.unifyTerms(three, four);

        Assertions.assertTrue(result.success());
        Assertions.assertFalse(result2.success());
    }

    @Test
    public void testMediumUnification() {//f(X, Y) = f(g(Y), a)
        Term left = parse("f(X, Y)");
        Term right = parse("f(g(Y), a)");
        UnificationResult result = unificationService.unifyTerms(left, right);

        Term xMapping = result.substitution().get("X");
        Term yMapping = result.substitution().get("Y");

        Assertions.assertTrue(result.success());
        Assertions.assertEquals("g(a)", xMapping.toCode());
        Assertions.assertEquals("a", yMapping.toCode());
    }

    @Test
    public void testAdvancedUnification() {
        Term left = parse("f(X, X, X)");
        Term right = parse("f(Y, g(Y), a)");
        UnificationResult result = unificationService.unifyTerms(left, right);

        Assertions.assertFalse(result.success());
    }

    Term parse(String str) {
        List<Token> tokens = lexer.tokenize(str);
        return termParser.parse(tokens);
    }

    @Test
    public void shouldUnifyLists() {
        Term left = parse("[L|K]");
        Term right = parse(" [1, 4, 5]");
        UnificationResult result = unificationService.unifyTerms(left, right);

        Assertions.assertTrue(result.success());
        Assertions.assertEquals(new IntegerTerm(1), result.substitution().get("L"));
    }
}