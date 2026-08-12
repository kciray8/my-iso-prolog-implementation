package iaroslav.baranov.tracklog.lexer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Lexer {
    public List<Token> tokenize(String text) {
        text = text + "\n"; //Add a new line to help handle the "." disambiguity

        List<Token> tokens = new ArrayList<>();

        int pos = 0;
        while (pos < text.length()) {
            char c = text.charAt(pos);

            //TODO Must refactor
            if (c == '/' && nextCharEqual(text, pos, '*')) {
                pos += 2;
                while (pos + 1 < text.length() &&
                        ((text.charAt(pos) != '*') || (text.charAt(pos + 1) != '/'))) {
                    pos++;
                }
                pos += 2;
                continue;
            }

            //TODO Must refactor
            if (isGraphicTokenChar(c)) {
                char nextChar = text.charAt(pos + 1);
                boolean dotFollowedByLayoutChar = c == '.' && isLayoutChar(nextChar);
                if (!dotFollowedByLayoutChar) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(c);
                    while (pos + 1 < text.length() && isGraphicTokenChar(text.charAt(pos + 1))) {
                        pos++;
                        sb.append(text.charAt(pos));
                    }

                    tokens.add(new Token(TokenType.GRAPHIC_TOKEN, sb.toString()));
                    pos++;
                    continue;
                }
            }

            if (isSmallLetterChar(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (pos + 1 < text.length() && isAlphanumericChar(text.charAt(pos + 1))) {
                    pos++;
                    sb.append(text.charAt(pos));
                }

                tokens.add(new Token(TokenType.LETTER_DIGIT_TOKEN, sb.toString()));
            } else if (isDecimalDigitChar(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                boolean floatNumber = false;
                while (pos + 1 < text.length()
                        && (isDecimalDigitChar(text.charAt(pos + 1)) ||
                        (pos + 2 < text.length()
                                && text.charAt(pos + 1) == '.'
                                && isDecimalDigitChar(text.charAt(pos + 2))))) {
                    pos++;
                    char nextChar = text.charAt(pos);
                    sb.append(nextChar);
                    if(nextChar == '.') {
                        floatNumber = true;
                    }
                }
                if(floatNumber) {
                    tokens.add(new Token(TokenType.FLOAT_NUMBER_TOKEN, sb.toString()));
                } else{
                    tokens.add(new Token(TokenType.INTEGER_TOKEN, sb.toString()));
                }
            } else if (c == '(') {
                tokens.add(new Token(TokenType.OPEN_TOKEN, "("));
            } else if (c == ')') {
                tokens.add(new Token(TokenType.CLOSE_TOKEN, ")"));
            } else if (c == '|') {
                tokens.add(new Token(TokenType.HEAD_TAIL_SEPARATOR_TOKEN, "|"));
            } else if (c == '.') {
                tokens.add(new Token(TokenType.END_TOKEN, "."));
            } else if (c == '[') {
                tokens.add(new Token(TokenType.OPEN_LIST_TOKEN, "["));
            } else if (c == ']') {
                tokens.add(new Token(TokenType.CLOSE_LIST_TOKEN, "]"));
            } else if (c == '{') {
                tokens.add(new Token(TokenType.OPEN_CURLY, "{"));
            } else if (c == '}') {
                tokens.add(new Token(TokenType.CLOSE_CURLY, "}"));
            } else if (c == '!') {
                tokens.add(new Token(TokenType.CUT_TOKEN, "!"));
            } else if (c == ',') {
                tokens.add(new Token(TokenType.COMMA_TOKEN, ","));
            } else if (c == ';') {
                tokens.add(new Token(TokenType.SEMICOLON_TOKEN, ";"));
            } else if (isVariableIndicatorChar(c) || isCapitalLetterChar(c)) {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                while (pos + 1 < text.length() && isAlphanumericChar(text.charAt(pos + 1))) {
                    pos++;
                    sb.append(text.charAt(pos));
                }

                tokens.add(new Token(TokenType.VARIABLE_TOKEN, sb.toString()));
            } else if (c == '%') { //ignoring the comments
                while (pos + 1 < text.length() && (text.charAt(pos + 1) != '\n')) {
                    pos++;
                }
            } else if (c == '"') {
                StringBuilder sb = new StringBuilder();
                while (pos + 1 < text.length() && (text.charAt(pos + 1) != '"')) {
                    pos++;
                    sb.append(text.charAt(pos));
                }
                pos++;

                tokens.add(new Token(TokenType.DOUBLE_QUOTED_LIST_TOKEN, sb.toString()));
            } else if (c == '\'') {
                StringBuilder sb = new StringBuilder();
                while (pos + 1 < text.length() && (text.charAt(pos + 1) != '\'')) {
                    pos++;
                    sb.append(text.charAt(pos));
                }
                pos++;

                tokens.add(new Token(TokenType.QUOTED_TOKEN, sb.toString()));
            } else if (c == ' ' || c == '\n' || c == '\r') {
                //ignore
            } else {
                throw new RuntimeException("Unrecognized char: " + c);
            }
            pos++;
        }

        return tokens;
    }

    boolean nextCharEqual(String str, int pos, char c) {
        if (pos + 1 >= str.length()) {
            return false;
        }
        return str.charAt(pos + 1) == c;
    }

    boolean isVariableIndicatorChar(char c) {
        return c == '_';
    }

    boolean isSmallLetterChar(char c) {
        return c >= 'a' && c <= 'z';
    }

    boolean isCapitalLetterChar(char c) {
        return c >= 'A' && c <= 'Z';
    }

    boolean isLetterChar(char c) {
        return isCapitalLetterChar(c) || isSmallLetterChar(c);
    }

    boolean isAlphaChar(char c) {
        return isLetterChar(c) || c == '_';
    }

    boolean isAlphanumericChar(char c) {
        return isAlphaChar(c) || isDecimalDigitChar(c);
    }

    boolean isDecimalDigitChar(char c) {
        return c >= '0' && c <= '9';
    }

    boolean isLayoutChar(char c) {
        return c == ' ' || c == '\n' || c == '\r';
    }

    boolean isGraphicTokenChar(char c) {
        return isGraphicChar(c) || c == '\\';
    }

    boolean isGraphicChar(char c) {
        return switch (c) {
            case '#', '$', '&', '*', '+', '-', '.', '/',
                 ':', '<', '=', '>', '?', '@',
                 '^', '~' -> true;
            default -> false;
        };
    }
}
