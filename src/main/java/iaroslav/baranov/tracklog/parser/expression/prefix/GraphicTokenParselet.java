package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.PrattParserException;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.List;

@Prefix(starterToken = TokenType.GRAPHIC_TOKEN)
public class GraphicTokenParselet extends CompoundTermParselet{

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        if(token.value().equals(":-")) {
            Term directiveBody = parser.parse(parserState, stopList);
            return new CompoundTerm(new NamedAtom(":-"), List.of(directiveBody));
        }
        if(token.value().equals("-")) {
            if(parserState.tokensAvailable() && parserState.peek().type() == TokenType.INTEGER_TOKEN){
                Token integerToken = parserState.consumeNext();
                int num = Integer.parseInt(integerToken.value());
                return new IntegerTerm(-num);
            } else {
                return new NamedAtom("-");
            }
        }

        String name = token.value();
        return parseAsNamedAtomOrCompoundTerm(parser, parserState, stopList, token, name);
    }
}
