package iaroslav.baranov.tracklog.parser.text;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ast.text.PrologTextClause;
import iaroslav.baranov.tracklog.ast.text.PrologTextDirective;
import iaroslav.baranov.tracklog.ast.text.PrologTextNil;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.processor.CompleteDatabase;
import iaroslav.baranov.tracklog.processor.ExecutionContext;
import iaroslav.baranov.tracklog.processor.Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class TextParser {
    private final TermParser termParser;
    private final Lexer lexer;

    public PrologText parse(String text) {
        List<Token> tokens = lexer.tokenize(text);
        return parse(tokens);
    }

    public PrologText parse(List<Token> tokens) {
        ParserState parserState = new ParserState(tokens, 0);
        try {
            return parse(parserState);
        } catch (Exception e) {
            parserState.printDebugInfo();
            throw e;
        }
    }

    //Recursive descent parsing
    public PrologText parse(ParserState parserState) {
        if (!parserState.tokensAvailable()) {
            return new PrologTextNil();
        }

        List<TokenType> stopList = new ArrayList<>();
        stopList.add(TokenType.END_TOKEN);

        Term term = termParser.parse(parserState, stopList);
        if (!parserState.tokensAvailable()) {
            throw new PrologTextParserException("Expected end token, but no tokens are available");
        }
        if (!parserState.currentToken().isEndToken()) {
            throw new PrologTextParserException("Expected end token, but encountered " + parserState.currentToken());
        }
        parserState.consumeNext();//consume "."
        PrologText tail = parse(parserState);

        PrologText text;
        if(term.getPrincipalFunctor().equals(":-/1")) {
            text = new PrologTextDirective(term, tail);
        } else {
            text = new PrologTextClause(term, tail);
        }
        return text;
    }
}
