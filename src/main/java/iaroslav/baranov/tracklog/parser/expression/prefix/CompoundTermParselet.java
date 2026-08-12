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

import static iaroslav.baranov.tracklog.lexer.TokenType.*;
import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_TOKEN;
import static iaroslav.baranov.tracklog.lexer.TokenType.COMMA_TOKEN;

public abstract class CompoundTermParselet implements PrefixParselet{
    Term parseAsNamedAtomOrCompoundTerm(
            TermParser parser,
            ParserState parserState,
            List<TokenType> stopList,
            Token token,
            String name
    ) {
        if(parserState.tokensAvailable() && parserState.peek().type() == OPEN_TOKEN) {
            List<Term> args = new ArrayList<>();
            Token openToken = parserState.consumeNext();
            if (openToken.type() != OPEN_TOKEN) {
                throw new PrattParserException("Not an open token: " + token);
            }

            while (parserState.peek().type() != CLOSE_TOKEN) {
                List<TokenType> extendedStopList = new ArrayList<>(stopList);
                extendedStopList.add(CLOSE_TOKEN);
                extendedStopList.add(COMMA_TOKEN);

                Term arg = parser.parse(parserState, 0, extendedStopList);
                args.add(arg);

                if (parserState.peek().type() == COMMA_TOKEN) {
                    parserState.consumeNext();
                }
            }

            Token closeToken = parserState.consumeNext();
            if (closeToken.type() != CLOSE_TOKEN) {
                throw new PrattParserException("Not a close token: " + token);
            }

            return new CompoundTerm(new NamedAtom(name), args);
        } else {
            return new NamedAtom(name);
        }
    }
}
