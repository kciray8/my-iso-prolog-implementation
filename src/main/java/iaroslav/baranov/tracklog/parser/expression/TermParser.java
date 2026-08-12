package iaroslav.baranov.tracklog.parser.expression;

import iaroslav.baranov.tracklog.ast.term.CompoundTerm;
import iaroslav.baranov.tracklog.ast.term.Term;
import iaroslav.baranov.tracklog.ast.term.atom.NamedAtom;
import iaroslav.baranov.tracklog.lexer.Lexer;
import iaroslav.baranov.tracklog.lexer.Token;
import iaroslav.baranov.tracklog.lexer.TokenType;
import iaroslav.baranov.tracklog.parser.ParserState;
import iaroslav.baranov.tracklog.parser.expression.infix.Infix;
import iaroslav.baranov.tracklog.parser.expression.infix.InfixParselet;
import iaroslav.baranov.tracklog.parser.expression.prefix.*;
import iaroslav.baranov.tracklog.service.operator.Operator;
import iaroslav.baranov.tracklog.service.operator.OperatorService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static iaroslav.baranov.tracklog.lexer.TokenType.*;

@Component
@RequiredArgsConstructor
public class TermParser {
    private static final Logger log = LoggerFactory.getLogger(TermParser.class);

    private final Map<TokenType, PrefixParselet> prefixParselets = new LinkedHashMap<>();
    private final Map<TokenType, InfixParselet> infixParselets = new LinkedHashMap<>();

    private final OperatorService operatorService;

    private final List<PrefixParselet> allPrefixComponents;
    private final List<InfixParselet> allInfixComponents;

    private final Lexer lexer;

    @PostConstruct
    public void init() {
        printAllOperators();

        for(InfixParselet infixParselet : allInfixComponents) {
            Class<?> targetClass = AopUtils.getTargetClass(infixParselet);
            Infix infix = AnnotationUtils.findAnnotation(targetClass, Infix.class);
            registerInfixParselet(infix.starterToken(), infixParselet);
        }

        for(PrefixParselet prefixParselet : allPrefixComponents) {
            Class<?> targetClass = AopUtils.getTargetClass(prefixParselet);
            Prefix prefix = AnnotationUtils.findAnnotation(targetClass, Prefix.class);
            registerPrefixParselet(prefix.starterToken(), prefixParselet);
        }
    }

    void registerPrefixParselet(TokenType starterToken, PrefixParselet parselet) {
        prefixParselets.put(starterToken, parselet);
    }

    void registerInfixParselet(TokenType starterToken, InfixParselet parselet) {
        infixParselets.put(starterToken, parselet);
    }

    void printAllOperators() {
        for (Operator operator : operatorService.getOperators().values()) {
            double[] bp = getBindingPower(operator.name());
            log.info("{} {} - {}", operator.name(), bp[0], bp[1]);
        }
    }

    public Term parse(String text) {
        return parse(lexer.tokenize(text));
    }

    public Term parse(List<Token> tokens) {
        ParserState parserState = new ParserState(tokens, 0);
        return parse(parserState);
    }

    public Term parse(ParserState parserState){
        return parse(parserState, new ArrayList<>());
    }

    public Term parse(ParserState parserState, List<TokenType> stopList){
        return parse(parserState, 0, stopList);
    }

    //Pratt Parser
    public Term parse(ParserState parserState, double minBindingPower, List<TokenType> stopList) {
        Token t = parserState.consumeNext();
        Term left = nud(t, parserState, stopList);

        while(true) {
            if(!parserState.tokensAvailable()) {
                break;
            }

            Token nextToken = parserState.peek();
            if(stopList.contains(nextToken.type())) {
                break;
            }
            double[] bp = getBindingPower(nextToken);

            double leftBP = bp[0];
            double rightBP = bp[1];
            if(leftBP < minBindingPower) {
                break;
            }
            Token op = parserState.consumeNext();

            Term right = parse(parserState, rightBP, stopList);
            left = led(op, left, right, parserState, stopList);
        }

        return left;
    }

    double[] getBindingPower(Token token) {
        String tokenValue = token.value();
        return getBindingPower(tokenValue);
    }

    double[] getBindingPower(String tokenValue) {
        if(!operatorService.getOperators().containsKey(tokenValue)) {
            throw new PrattParserException("Unexpected token: " + tokenValue);
        }

        Operator operator = operatorService.getOperators().get(tokenValue);
        int rightAddition = 0;
        if(operator.associativity()  == OperatorAssociativity.LEFT) {
            rightAddition = 1;
        }
        int bp = 1200 - operator.priority();

        return new double[]{bp, bp + rightAddition};
    }

    boolean isNameToken(Token token) {
        TokenType type = token.type();
        return type == LETTER_DIGIT_TOKEN
                || type == GRAPHIC_TOKEN
                || type == QUOTED_TOKEN
                || type == SEMICOLON_TOKEN
                || type == CUT_TOKEN;
    }

    Term nud(Token token, ParserState parserState, List<TokenType> stopList){
        String value = token.value();
        if(operatorService.isUnaryOperator(value)){
            double minBindingPower = getBindingPower(token)[0];
            Term arg = parse(parserState, minBindingPower, stopList);
            List<Term> args = new ArrayList<>();
            args.add(arg);
            return new CompoundTerm(new NamedAtom(value), args);
        }

        if(prefixParselets.containsKey(token.type())) {
            PrefixParselet parselet = prefixParselets.get(token.type());
            return parselet.parse(this, parserState, stopList, token);
        }

        throw new PrattParserException("Unexpected token: " + token);
    }

    Term led(
            Token token, 
            Term left, 
            Term right, 
            ParserState parserState, 
            List<TokenType> stopList
    ){
        String value = token.value();
        if(operatorService.isBinaryOperator(value)){
            List<Term> args = new ArrayList<>();
            args.add(left);
            args.add(right);
            return new CompoundTerm(new NamedAtom(value), args);
        }

        if(infixParselets.containsKey(token.type())) {
            InfixParselet parselet = infixParselets.get(token.type());
            return parselet.parse(this, parserState, stopList, left, right, token);
        }

        throw new PrattParserException("Unexpected token: " + token);
    }
}
