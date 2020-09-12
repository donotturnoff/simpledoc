package net.donotturnoff.simpledoc.browser.parsing;

public class NonTerminal extends Symbol<Void> {
    
    protected NonTerminal() {}
    
    public NonTerminal(String name) {
        this.name = name.toLowerCase();
    }
}
