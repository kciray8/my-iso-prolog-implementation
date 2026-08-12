package iaroslav.baranov.tracklog.parser;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.text.PrologText;
import iaroslav.baranov.tracklog.ast.text.PrologTextClause;
import iaroslav.baranov.tracklog.ast.text.PrologTextDirective;
import iaroslav.baranov.tracklog.ast.text.PrologTextNil;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.parser.text.PrologTextParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class SourceParser {
    private final TermParser termParser;
    private final Lexer lexer;

    public void parse(
            String text,
            Consumer<Term> termConsumer
    ) {
        List<Token> tokens = lexer.tokenize(text);
        parse(tokens, termConsumer);
    }

    public void parse(
            List<Token> tokens,
            Consumer<Term> termConsumer
    ) {
        ParserState parserState = new ParserState(tokens, 0);
        try {
            parse(parserState,  termConsumer);
        } catch (Exception e) {
            parserState.printDebugInfo();
            throw e;
        }
    }

    //Recursive descent parsing
    public void parse(ParserState parserState,
                      Consumer<Term> termConsumer) {
        List<TokenType> stopList = new ArrayList<>();
        stopList.add(TokenType.END_TOKEN);

        while (parserState.tokensAvailable()) {
            Term term = termParser.parse(parserState, stopList);
            if (!parserState.tokensAvailable()) {
                throw new PrologTextParserException("Expected end token, but no tokens are available");
            }
            if (!parserState.currentToken().isEndToken()) {
                throw new PrologTextParserException("Expected end token, but encountered " + parserState.currentToken());
            }
            termConsumer.accept(term);
            parserState.consumeNext();//consume "."
        }
    }
}
