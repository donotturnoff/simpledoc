package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TextElement extends Element {
    private final String text;

    public TextElement(Page page, String text) {
        super(page, "text", Map.of(), List.of());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        Font font = style.getFont();
        Color colour = style.getColour();

        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(colour);
        parentPanel.add(label);
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
