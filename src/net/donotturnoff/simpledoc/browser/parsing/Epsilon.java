package net.donotturnoff.simpledoc.browser.parsing;
// Strictly speaking, epsilon is not a symbol, but this makes the code simpler

public class Epsilon extends Terminal<Void> {
    
    public Epsilon() {
        this.name = "\u03B5";
    }

}
