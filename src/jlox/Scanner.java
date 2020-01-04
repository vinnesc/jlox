package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jlox.TokenType.*;

public class Scanner {
    private String source;
    private final List<Token> tokens = new ArrayList<>();
    private final Map<String, TokenType> keywords = new HashMap<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;

        /**
         * I like this here more because it's part of the context for the Scanner and I had to look up when a static
         * block is initialized, so probably wasn't the best idea.
        */
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    public boolean isEnd() {
        return current >= source.length();
    }

    public char nextChar() {
        return source.charAt(current++);
    }

    public void addToken(TokenType type) {
        addToken(type, null);
    }

    public void addToken(TokenType type, Object literal) {
        Token token = new Token(type, source.substring(start, current), literal, line);
        tokens.add(token);
    }

    public boolean match(char c) {
        if (isEnd()) return false;
        if (source.charAt(current) != c) return false;

        current++;
        return true;
    }

    public char peek() {
        if (isEnd()) return '\0';
        else return source.charAt(current);
    }

    public char peekNext() {
        if (current + 1 == source.length()) return '\0';
        else return source.charAt(current + 1);
    }

    public void string() {
        while (peek() != '"' && !isEnd()) {
            if (peek() == '\n') line++;
            nextChar();
        }

        if (isEnd()) {
            Lox.error(line, "Unterminated string");
            return;
        }

        // Skip ".
        nextChar();

        // Content between quotes.
        String string = source.substring(start + 1, current - 1);
        addToken(STRING, string);
    }

    public void number() {
        while (isDigit(peek())) nextChar();

        if (peek() == '.' && isDigit(peekNext())) {
            nextChar();

            while(isDigit(peek())) nextChar();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    public void identifier() {
        while (isAlphaNumeric(peek())) nextChar();

        String text = source.substring(start, current);

        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    public boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean isAlpha(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_';
    }

    public boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    public void scanToken() {
        char c = nextChar();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            case '/': {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isEnd()) nextChar();
                } if (match('*')) {
                    // TODO: Seems like it works with at least double nesting. Keep testing?
                    // Might be trivial, but I'm implementing this off the top of my head.
                    int nested = 1;
                    while (nested > 0 && !isEnd()) {
                        char next = nextChar();
                        if (next == '\n') line++;

                        if (next == '*' && peek() == '/') {
                            nested--;
                        }
                        else if (next == '/' && peek() == '*') {
                            nested++;
                        }
                    }

                    if (nested != 0) {
                        Lox.error(line, "Unterminated comment.");
                        return;
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            }

            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n': {
                line++;
                break;
            }

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, "Unknown character.");
                }
        }
    }

    public List<Token> scan() {
        while (!isEnd()) {
            start = current;
            scanToken();
        }

        return tokens;
    }
}
