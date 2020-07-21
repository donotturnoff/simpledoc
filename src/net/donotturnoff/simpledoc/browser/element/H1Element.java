package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H1Element extends Element {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("font_family", Font.SERIF);
        defaultStyle.put("font_size", "40");
        defaultStyle.put("font_style", "bold");
    }

    public H1Element(Map<String, String> attributes, List<Element> children) {
        super("h1", attributes, children);
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
