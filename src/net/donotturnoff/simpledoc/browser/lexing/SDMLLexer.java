package net.donotturnoff.simpledoc.browser.lexing;

import net.donotturnoff.simpledoc.browser.element.Element;

public class SDMLLexer {
    private final String body;
    private int i;
    private char nextChar;
    private boolean end;

    public SDMLLexer(String body) {
        this.body = body;
        this.i = 0;
        getChar();
    }

    private void getChar() {
        if (i == body.length()) {
            end = true;
        } else {
            nextChar = body.charAt(i);
            i++;
            end = false;
        }
    }

    public Token<?> nextToken() throws LexingException {
        Token<?> t;
        consumeWhiteSpace();
        if (end) {
            t = new Token<Void>(TokenType.EOF);
        } else {
            switch (nextChar) {
                case '(':
                    t = new Token<Void>(TokenType.LPAREN); getChar();
                    break;
                case ')':
                    t = new Token<Void>(TokenType.RPAREN); getChar();
                    break;
                case '{':
                    t = new Token<Void>(TokenType.LBRACE); getChar();
                    break;
                case '}':
                    t = new Token<Void>(TokenType.RBRACE); getChar();
                    break;
                case '=':
                    t = new Token<Void>(TokenType.EQUALS); getChar();
                    break;
                case ',':
                    t = new Token<Void>(TokenType.COMMA); getChar();
                    break;
                case '"':
                case '\'':
                    t = consumeString(nextChar);
                    break;
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    t = consumeIdentifier(nextChar);
                    break;
                default:
                    throw new LexingException("Unrecognised token initial: \"" + nextChar + "\"");
            }
        }
        return t;
    }

    private Token<String> consumeIdentifier(char initial) throws LexingException {
        StringBuilder s = new StringBuilder();
        s.append(initial);
        getChar();
        while (!end && (Character.isAlphabetic(nextChar) || Character.isDigit(nextChar))) {
            s.append(nextChar);
            getChar();
        }
        String ident = s.toString();
        if (Element.isLegalTag(ident) || Element.isLegalAttribute(ident)) {
            return new Token<>(TokenType.IDENT, ident);
        } else {
            throw new LexingException("Invalid identifier: \"" + ident + "\"");
        }
    }

    private Token<String> consumeString(char quoteStyle) throws LexingException {
        StringBuilder s = new StringBuilder();
        boolean escaped = false;
        getChar();
        while (nextChar != quoteStyle || escaped) {
            if (end) {
                throw new LexingException("Unterminated string literal: \"" + s.toString() + "\"");
            }
            if (nextChar == '\\') {
                escaped = true;
            } else {
                escaped = false;
                s.append(nextChar);
            }
            getChar();
        }
        getChar();
        return new Token<>(TokenType.STRING, s.toString());
    }

    private void consumeWhiteSpace() {
        while (!end && Character.isWhitespace(nextChar)) {
            getChar();
        }
    }
}
