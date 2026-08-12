package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.PrattParserException;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.ArrayList;
import java.util.List;

import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_CURLY;
import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_TOKEN;

@Prefix(starterToken = TokenType.OPEN_CURLY)
public class CurlyBracketsParselet implements PrefixParselet{

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        List<TokenType> newStopList = new ArrayList<>();
        newStopList.add(CLOSE_CURLY);

        Term body = parser.parse(parserState, newStopList);
        Token closeToken = parserState.consumeNext();
        if(closeToken.type() != CLOSE_CURLY) {
            throw new PrattParserException("Not a close curly token: " + closeToken);
        }
        CompoundTerm ct = new CompoundTerm("{}", List.of(body));
        return ct;
    }
}
