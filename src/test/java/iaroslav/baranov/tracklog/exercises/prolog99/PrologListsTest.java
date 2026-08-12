package iaroslav.baranov.tracklog.exercises.prolog99;

import iaroslav.baranov.tracklog.BaseTest;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.processor.ExecutionResult;
import iaroslav.baranov.tracklog.processor.Processor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PrologListsTest extends BaseTest {
    //P01 (*) Find the last element of a list.
    /*@Test
    public void shouldFindTheLastElement() {
        CompleteDatabaseService db = new CompleteDatabaseService();
        String code = """
                my_last(H, [H]).
                my_last(L, [_|T]) :- my_last(L, T).
                """;
        List<Token> tokens = lexer.tokenize(code);
        PrologText text = textParser.parse(tokens);
        db.init(text);
        db.print();

        Processor processor = createProcessor();

        ExecutionResult res = processor.execute(parseTerm("my_last(L, [1,4,5])"), db);
        Assertions.assertTrue(res.success());
    }*/
}
