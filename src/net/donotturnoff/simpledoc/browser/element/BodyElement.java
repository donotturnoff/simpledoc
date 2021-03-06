package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class BodyElement extends VisibleElement {
    public BodyElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "body", attributes, children);
        setDefault("padding_top", "5");
        setDefault("padding_bottom", "5");
        setDefault("padding_left", "5");
        setDefault("padding_right", "5");
        setDefault("layout", "vbox");
    }

    @Override
    public JPanel getPanel() {
        return page.getPanel();
    }

    @Override
    public void render(JPanel parentPanel) {
        panel = getPanel();
        style();
        renderChildren(panel);
    }
}
