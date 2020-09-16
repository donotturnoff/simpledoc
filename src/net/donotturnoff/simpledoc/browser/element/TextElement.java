package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

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

    @Override
    public void render(Page page, JPanel parentPanel) {
        Style style = getStyle();
        Font font = style.getFont();
        Color colour = style.getColour();

        label = new JLabel(text);
        label.setFont(font);
        label.setForeground(colour);
        parentPanel.add(label);
    }

    @Override
    public void refresh(Page page) {
        Style style = getStyle();
        Font font = style.getFont();
        Color colour = style.getColour();

        if (label != null) {
            label.setFont(font);
            label.setForeground(colour);

            label.revalidate();
            label.repaint();
        }
    }

    @Override
    protected String toString(String indent) {
        return indent + text;
    }
}
