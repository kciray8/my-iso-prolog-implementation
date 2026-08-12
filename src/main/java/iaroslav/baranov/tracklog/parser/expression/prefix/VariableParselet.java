package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.List;

@Prefix(starterToken = TokenType.VARIABLE_TOKEN)
public class VariableParselet implements PrefixParselet{
    int anonymousVariablesCounter;

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        if(token.value().equals("_")) {
            return new Variable("_A" + anonymousVariablesCounter++);
        } else {
            return new Variable(token.value());
        }
    }
}
