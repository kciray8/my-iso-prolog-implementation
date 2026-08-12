package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.PrattParserException;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static iaroslav.baranov.tracklog.lexer.TokenType.*;
import static iaroslav.baranov.tracklog.lexer.TokenType.CLOSE_TOKEN;
import static iaroslav.baranov.tracklog.lexer.TokenType.COMMA_TOKEN;

@Prefix(starterToken = LETTER_DIGIT_TOKEN)
public class LetterDigitParselet extends CompoundTermParselet{
    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        String name = token.value();
        return parseAsNamedAtomOrCompoundTerm(parser, parserState, stopList, token, name);
    }
}
