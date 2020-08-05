package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyElement extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("padding_top", "5");
        defaultStyle.put("padding_bottom", "5");
        defaultStyle.put("padding_left", "5");
        defaultStyle.put("padding_right", "5");
        defaultStyle.put("layout", "vbox");
    }

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
