package net.donotturnoff.simpledoc.browser.parsing;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.LinkedList;
import java.util.Queue;
import net.donotturnoff.lr0.*;

public abstract class Lexer {
    protected final Page page;
    protected String body;
    protected int i, line, column;
    protected char nextChar;
    protected boolean end;

    public Lexer(Page page) {
        this.page = page;
        this.i = 0;
        this.line = 1;
        this.column = 0;
        this.end = false;
    }

    protected void getChar() {
        if (i == body.length()) {
            end = true;
        } else {
            nextChar = body.charAt(i);
            i++;
            column++;
            end = false;
        }
        if (nextChar == '\n') {
            line++;
            column = 0;
        }
    }

    public Queue<Terminal<?>> lex(String body) throws LexingException {
        this.body = body;
        getChar();
        Queue<Terminal<?>> tokens = new LinkedList<>();
        Terminal<?> t;
        do {
            t = nextToken();
            tokens.add(t);
        } while (!(t instanceof EOF));
        return tokens;
    }

    protected abstract Terminal<?> nextToken() throws LexingException;

    protected Terminal<String> consumeIdentifier(char initial) {
        StringBuilder s = new StringBuilder();
        s.append(initial);
        getChar();
        while (!end && (Character.isAlphabetic(nextChar) || Character.isDigit(nextChar) || nextChar == '_')) {
            s.append(nextChar);
            getChar();
        }
        String ident = s.toString();
        return new Terminal<>("IDENT", line, column, ident);
    }

    protected Terminal<String> consumeString(char quoteStyle) throws LexingException {
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
        return new Terminal<>("STRING", line, column, s.toString());
    }

    protected void consumeWhiteSpace() {
        while (!end && Character.isWhitespace(nextChar)) {
            getChar();
        }
    }
}
