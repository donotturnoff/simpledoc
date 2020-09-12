package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class BodyElement extends BoxElement {
    public BodyElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "body", attributes, children);
        style.setDefault("padding_top", "5");
        style.setDefault("padding_bottom", "5");
        style.setDefault("padding_left", "5");
        style.setDefault("padding_right", "5");
        style.setDefault("layout", "vbox");
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = page.getPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        style(panel);
        for (Element c: children) {
            c.render(page, panel);
        }
    }
}
