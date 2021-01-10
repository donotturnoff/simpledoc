package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TextElement extends Element {
    private String text;
    private JLabel label;

    public TextElement(Page page, String text) {
        super(page, "text", Map.of(), List.of());
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
        label.setText(text);
    }

    public String getText() {
        return text;
    }

    public void style() {
        Font font = style.getFont();
        Color colour = style.getColour();
        label.setFont(font);
        label.setForeground(colour);
    }

    @Override
    public void render(JPanel parentPanel) {
        label = new JLabel(text);
        style();
        parentPanel.add(label);
    }

    @Override
    public void refresh() {
        style();
        label.revalidate();
        label.repaint();
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
