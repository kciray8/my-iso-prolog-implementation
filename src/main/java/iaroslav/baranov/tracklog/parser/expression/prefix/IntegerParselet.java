package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.List;

@Prefix(starterToken = TokenType.INTEGER_TOKEN)
public class IntegerParselet implements PrefixParselet{

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        int num = Integer.parseInt(token.value());
        return new IntegerTerm(num);
    }
}
