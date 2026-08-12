package iaroslav.baranov.tracklog;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.TextParser;
import iaroslav.baranov.tracklog.processor.Processor;
import iaroslav.baranov.tracklog.service.bip.BuildInPredicateService;
import iaroslav.baranov.tracklog.service.TermService;
import iaroslav.baranov.tracklog.service.unification.UnificationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

public class BaseTest {
    @Mock
    protected TextParser textParser;
    @Mock
    protected TermParser termParser;
    @Mock
    protected Lexer lexer;
    @Mock
    protected UnificationService unificationService;
    @Mock
    protected TermService termService;
    @Mock
    protected BuildInPredicateService buildInPredicateService;
    @Mock
    protected Processor processor;

    /*public Term parseTerm(String code){
        List<Token> tokens = lexer.tokenize(code);
        return termParser.parse(tokens);
    }*/
}
