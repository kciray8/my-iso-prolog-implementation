package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.List;

@Prefix(starterToken = TokenType.CUT_TOKEN)
public class CutParselet implements PrefixParselet{
    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        return new NamedAtom("!");
    }
}
