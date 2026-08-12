package iaroslav.baranov.tracklog.lexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class LexerTest {
    @Test
    void shouldTokenizeSimpleClause(){
        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize("my_last(L, [_|T]) :- my_last(L, T).");
        Assertions.assertEquals(18, tokens.size());
        Assertions.assertEquals(new Token(TokenType.END_TOKEN, "."),
                tokens.get(tokens.size()-1));
    }
}
