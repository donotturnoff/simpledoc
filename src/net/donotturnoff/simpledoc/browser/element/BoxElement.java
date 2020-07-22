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

    @Override
    public void render(Page page, JPanel parentPanel) {
        Map<String, String> style = getStyle();
        Cursor cursor = new Cursor(Element.cursorMap.getOrDefault(style.getOrDefault("cursor", "default"), Cursor.DEFAULT_CURSOR));

        JPanel panel = new JPanel();
        panel.setCursor(cursor);
        parentPanel.add(panel);
        for (Element c: children) {
            c.render(page, panel);
        }
    }
}
