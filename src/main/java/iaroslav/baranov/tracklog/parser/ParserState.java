package iaroslav.baranov.tracklog.parser;

import iaroslav.baranov.tracklog.lexer.Token;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
@Slf4j
public class ParserState {
    private List<Token> tokens;
    private int position;

    public ParserState(List<Token> tokens, int position) {
        this.tokens = tokens;
        this.position = position;
    }

    public Token currentToken(){
        return tokens.get(position);
    }

    public boolean tokensAvailable(){
        return position < tokens.size();
    }

    public Token consumeNext(){
        return tokens.get(position++);
    }
    public Token peek(){
        return tokens.get(position);
    }

    public void printDebugInfo() {
        log.info("Parsed successfully: " + tokens.subList(0, position - 1));
        log.info("Not parsed: " + tokens.subList(position - 1, tokens.size()));
    }
}
