package iaroslav.baranov.tracklog.exercises;

import iaroslav.baranov.tracklog.BaseTest;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

public class BasicDeductionExercises extends BaseTest {
    CompleteDatabaseService completeDatabaseService;

    @Test
    public void shouldSolveGraphPath() {
        /*CompleteDatabase db = new CompleteDatabase();
        String code = """
                edge(a, b).
                edge(b, c).
                
                path(X, Y) :- edge(X, Y).
                path(X, Y) :-
                    edge(X, Z),
                    path(Z, Y).
                """;
        List<Token> tokens = lexer.tokenize(code);
        PrologText text = textParser.parse(tokens);
        completeDatabaseService.init(db, text);
        completeDatabaseService.print(db);*/

        /*ExecutionResult fromAToD = processor.execute(parseTerm("path(a, c)"), db);
        Assertions.assertTrue(fromAToD.success());

        ExecutionResult fromAToM = processor.execute(parseTerm("path(a, m)"), db);
        Assertions.assertFalse(fromAToM.success());*/
    }
}
