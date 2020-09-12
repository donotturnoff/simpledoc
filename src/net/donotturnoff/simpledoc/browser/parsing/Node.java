package net.donotturnoff.simpledoc.browser.parsing;

import java.util.List;
import java.util.ArrayList;

public class Node {
    private final List<Node> children;
    private final Symbol<?> symbol;
    
    public Node(Symbol<?> symbol) {
        this.children = new ArrayList<>();
        this.symbol = symbol;
    }
    
    public Node(Symbol<?> symbol, List<Node> children) {
        this.children = children;
        this.symbol = symbol;
    }
    
    public void addChild(Node child) {
        children.add(child);
    }
    
    public List<Node> getChildren() {
        return children;
    }
    
    public Symbol<?> getSymbol() {
        return symbol;
    }
    
    @Override
    public String toString() {
        return toString("");
    }
    
    private String toString(String indent) {
        if (children.isEmpty()) {
            return indent + symbol.toString();
        }
        StringBuilder s = new StringBuilder(indent + symbol.toString() + " {\n");
        for (Node child: children) {
            s.append(child.toString(indent + " ")).append("\n");
        }
        s.append(indent).append("}");
        return s.toString();
    }
}
