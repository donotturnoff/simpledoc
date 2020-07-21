package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TextElement extends Element {

    private final String text;

    public TextElement(String text) {
        super("text", Map.of(), List.of());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected void draw(JPanel panel) {

    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
