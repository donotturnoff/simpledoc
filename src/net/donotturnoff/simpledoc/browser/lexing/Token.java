package net.donotturnoff.simpledoc.browser.lexing;

public class Token<T> {
    private final TokenType type;
    private final T value;

    public Token(TokenType type) {
        this.type = type;
        this.value = null;
    }

    public Token(TokenType type, T value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        if (value == null) {
            return type.toString();
        } else {
            return type.toString() + "(" + value.toString() + ")";
        }
    }
}
