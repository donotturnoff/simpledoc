package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

// For elements which do not get drawn on the page
public abstract class InvisibleElement extends Element {
    public InvisibleElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
    }

    @Override
    public final void render(JPanel parentPanel) {
        for (Element c: children) {
            c.render(parentPanel);
        }
    }

    @Override
    public final void refresh() {
        for (Element c: children) {
            c.refresh();
        }
    }
}
