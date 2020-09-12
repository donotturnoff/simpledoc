package net.donotturnoff.simpledoc.browser.parsing;

import java.util.List;
import java.util.Objects;

public class Production {
    private final NonTerminal head;
    private final List<Symbol<?>> body;
    
    public Production(NonTerminal head, List<Symbol<?>> body) {
        this.head = head;
        this.body = body;
    }
    
    public Symbol<?> getBodySymbol(int i) {
        return body.get(i);
    }
    
    public NonTerminal getHead() {
        return head;
    }
    
    public List<Symbol<?>> getBody() {
        return body;
    }
    
    @Override
    public String toString() {
        return head + " -> " + body;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Production)) {
            return false;
        }
        Production p = (Production) o;
        return head.equals(p.head) && body == p.body;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(head, body);
    }
}
