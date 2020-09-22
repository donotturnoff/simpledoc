package net.donotturnoff.simpledoc.browser.parsing;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.lr0.*;

public class SDMLLexer extends Lexer {

    public SDMLLexer(Page page) {
        super(page);
    }

    protected Terminal<?> nextToken() throws LexingException {
        Terminal<?> t;
        consumeWhiteSpace();
        if (end) {
            t = new EOF();
        } else {
            switch (nextChar) {
                case '(':
                    t = new Terminal<Void>("LPAREN", line, column); getChar();
                    break;
                case ')':
                    t = new Terminal<Void>("RPAREN", line, column); getChar();
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
}
