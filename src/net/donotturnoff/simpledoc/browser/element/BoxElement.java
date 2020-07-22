package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class BoxElement extends Element {
    public BoxElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
    }

    public void style(JComponent component) {
        Map<String, String> style = getStyle();
        Cursor cursor = new Cursor(Element.cursorMap.getOrDefault(style.getOrDefault("cursor", "default"), Cursor.DEFAULT_CURSOR));
        Color backgroundColour = Color.decode(style.getOrDefault("background_colour", "#FFFFFF"));

        component.setCursor(cursor);
        component.setBackground(backgroundColour);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = new JPanel();
        style(panel);
        parentPanel.add(panel);
        for (Element c: children) {
            c.render(page, panel);
        }
    }
}
