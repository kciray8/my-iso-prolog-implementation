package iaroslav.baranov.tracklog.parser.expression.infix;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.List;

public interface InfixParselet {
    Term parse(
            TermParser parser,
            ParserState parserState,
            List<TokenType> stopList,
            Term left,
            Term right,
            Token token
    );
}
