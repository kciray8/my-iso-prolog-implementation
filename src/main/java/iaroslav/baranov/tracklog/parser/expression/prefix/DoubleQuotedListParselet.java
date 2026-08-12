package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.IntegerTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.Variable;
import iaroslav.baranov.tracklog.ast.term.atom.EmptyListAtom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.TermParser;

import java.util.ArrayList;
import java.util.List;

@Prefix(starterToken = TokenType.DOUBLE_QUOTED_LIST_TOKEN)
public class DoubleQuotedListParselet implements PrefixParselet {

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        List<Term> listElements = new ArrayList<>();
        for(char c: token.value().toCharArray()) {
            String name = Character.toString(c);
            listElements.add(new NamedAtom(name));
        }

        return convertToFunctionalList(listElements);
    }

    Term convertToFunctionalList(List<Term> listElements) {
        Term last = new EmptyListAtom();
        for (Term term : listElements.reversed()) {
            last = new CompoundTerm(new NamedAtom("."), List.of(term, last));
        }
        return last;
    }
}
