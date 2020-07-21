package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PElement extends Element {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("font_family", Font.SANS_SERIF);
        defaultStyle.put("font_size", "12");
        defaultStyle.put("font_style", "plain");
    }

    public PElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "p", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        Map<String, String> style = getStyle();
        JPanel panel = new JPanel();
        parentPanel.add(panel);
        for (Element c: children) {
            c.render(page, panel);
        }
    }
}
