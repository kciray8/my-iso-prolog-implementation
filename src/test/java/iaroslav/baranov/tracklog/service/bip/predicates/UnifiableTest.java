package iaroslav.baranov.tracklog.service.bip.predicates;

import iaroslav.baranov.tracklog.BaseIntegrationTest;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.service.unification.UnificationResult;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UnifiableTest extends BaseIntegrationTest {
    @Autowired
    private UnificationService unificationService;

    @Test
    void anonymousVariableBug1() {
        Term term1 = termParser.parse("[3, 4, 5]");
        Term term2 = termParser.parse("[_|[X|_]]");
        UnificationResult unificationResult = unificationService.unifyTerms(term1, term2);
        assertTrue(unificationResult.success());
    }
}