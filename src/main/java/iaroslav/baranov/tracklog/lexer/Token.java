package iaroslav.baranov.tracklog.lexer;

public record Token(TokenType type, String value) {
    Token(TokenType type) {
        this(type, null);
    }

    @Override
    public String toString() {
        return type + " " + value;
    }

    public boolean isEndToken() {
        return type == TokenType.END_TOKEN;
    }
}
