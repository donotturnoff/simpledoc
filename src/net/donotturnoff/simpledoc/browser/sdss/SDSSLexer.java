package net.donotturnoff.simpledoc.browser.sdss;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.lr0.*;
import net.donotturnoff.simpledoc.browser.parsing.Lexer;
import net.donotturnoff.simpledoc.browser.parsing.LexingException;

public class SDSSLexer extends Lexer {

    public SDSSLexer(Page page) {
        super(page);
    }

    @Override
    protected Terminal<?> nextToken() throws LexingException {
        Terminal<?> t;
        consumeWhiteSpace(); // Ignores whitespace between tokens
        if (end) {
            t = new EOF(); // Parser requires special EOF token
        } else {
            switch (nextChar) {
                case '(':
                    t = new Terminal<Void>("LPAREN", line, column); getChar();
                    break;
                case ')':
                    t = new Terminal<Void>("RPAREN", line, column); getChar();
                    break;
                case '<':
                    t = new Terminal<Void>("LANGLE", line, column); getChar();
                    break;
                case '>':
                    t = new Terminal<Void>("RANGLE", line, column); getChar();
                    break;
                case '{':
                    t = new Terminal<Void>("LBRACE", line, column); getChar();
                    break;
                case '}':
                    t = new Terminal<Void>("RBRACE", line, column); getChar();
                    break;
                case ',':
                    t = new Terminal<Void>("COMMA", line, column); getChar();
                    break;
                case '=':
                    t = new Terminal<Void>("EQUALS", line, column); getChar();
                    break;
                case '?':
                    t = new Terminal<Void>("QMARK", line, column); getChar();
                    break;
                case '*':
                    t = new Terminal<Void>("ASTERISK", line, column); getChar();
                    break;
                case '"':
                case '\'':
                    t = consumeString(nextChar); // If token begins with ' or ", consume the whole string
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
                    t = consumeIdentifier(nextChar); // If token begins with a letter, consume the whole identifier
                    break;
                default:
                    throw new LexingException("Unrecognised token initial: \"" + nextChar + "\"");
            }
        }
        return t;
    }
}
