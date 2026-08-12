package iaroslav.baranov.tracklog.processor;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.service.db.CompleteDatabaseService;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import org.junit.jupiter.api.Test;

import java.util.List;

class ProcessorTest {
    /*Lexer lexer = new Lexer();
    TermParser termParser = new TermParser();
    TextParser textParser = new TextParser();
    UnificationService unificationService = new UnificationService();
    TermService termService = new TermService();
    BuildInPredicateService buildInPredicateService = new BuildInPredicateService();
    Processor processor = new Processor(unificationService, termService, buildInPredicateService);

    CompleteDatabaseService prepareDatabase(){
        CompleteDatabaseService db = new CompleteDatabaseService();

        Lexer lexer = new Lexer();
        String line1 = "my_last(H, [H]). ";
        String line2 = "my_last(L, [_|T]) :- my_last(L, T).";
        String code = line1 + line2;
        List<Token> tokens = lexer.tokenize(code);
        PrologText text = textParser.parse(tokens);

        db.addUserDefinedProcedures(text);

        return db;
    }

    @Test
    void simpleExecutionShouldSucceed(){
        String goal = "my_last(L, [1,40,5])";
        List<Token> tokens = lexer.tokenize(goal);

        ParserState parserState = new ParserState(tokens, 0);
        Term term = termParser.parse(parserState);

        CompleteDatabaseService db = prepareDatabase();

        ExecutionResult executionResult = processor.execute(term, db);

        System.out.println();
    }*/
}