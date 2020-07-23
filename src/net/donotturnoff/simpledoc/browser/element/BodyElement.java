package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyElement extends BoxElement {

    public BodyElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "body", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = page.getPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        style(panel);
        for (Element c: children) {
            c.render(page, panel);
        }
    }
}
