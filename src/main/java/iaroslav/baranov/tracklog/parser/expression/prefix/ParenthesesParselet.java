package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.PrattParserException;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.ArrayList;
import java.util.List;

import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_LIST_TOKEN;
import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_TOKEN;

@Prefix(starterToken = TokenType.OPEN_TOKEN)
public class ParenthesesParselet implements PrefixParselet{

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        List<TokenType> newStopList = new ArrayList<>();
        newStopList.add(CLOSE_TOKEN);

        Term body = parser.parse(parserState, newStopList);
        Token closeToken = parserState.consumeNext();
        if(closeToken.type() != CLOSE_TOKEN) {
            throw new PrattParserException("Not a close token: " + closeToken);
        }
        return body;
    }
}
