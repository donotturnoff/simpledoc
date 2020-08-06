package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class BodyElement extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("padding_top", "5");
        defaultStyle.set("padding_bottom", "5");
        defaultStyle.set("padding_left", "5");
        defaultStyle.set("padding_right", "5");
        defaultStyle.set("layout", "vbox");
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
