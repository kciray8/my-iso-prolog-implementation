package iaroslav.baranov.tracklog.parser.expression.prefix;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.EmptyListAtom;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.PrattParserException;
import iaroslav.baranov.tracklog.parser.expression.TermParser;
import iaroslav.baranov.tracklog.service.TermService;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static iaroslav.baranov.tracklog.lexer.TokenType.*;

@Prefix(starterToken = OPEN_LIST_TOKEN)
@AllArgsConstructor
public class ListParselet implements PrefixParselet{
    private TermService termService;

    @Override
    public Term parse(TermParser parser, ParserState parserState, List<TokenType> stopList, Token token) {
        List<Term> listElements = new ArrayList<>();

        List<TokenType> extendedStopList = new ArrayList<>(stopList);
        extendedStopList.add(CLOSE_LIST_TOKEN);
        extendedStopList.add(COMMA_TOKEN);
        extendedStopList.add(HEAD_TAIL_SEPARATOR_TOKEN);

        while(parserState.peek().type() != CLOSE_LIST_TOKEN) {
            Term arg = parser.parse(parserState, 0, extendedStopList);
            listElements.add(arg);

            if(parserState.peek().type() == COMMA_TOKEN) {
                parserState.consumeNext();
            }
            if(parserState.peek().type() == HEAD_TAIL_SEPARATOR_TOKEN) {
                break;
            }
        }

        boolean endsWithNil = true;
        TokenType tokenType = parserState.peek().type();
        if(tokenType == HEAD_TAIL_SEPARATOR_TOKEN){
            parserState.consumeNext();
            Term tail = parser.parse(parserState, 0, extendedStopList);
            listElements.add(tail);
            endsWithNil = false;
        }

        Token closeToken = parserState.consumeNext();
        if(closeToken.type() != CLOSE_LIST_TOKEN) {
            throw new PrattParserException("Not a close list token: " + closeToken);
        }

        return termService.convertToFunctionalList(listElements, endsWithNil);
    }
}
