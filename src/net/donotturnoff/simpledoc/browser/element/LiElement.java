package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.SDMLStyler;
import net.donotturnoff.simpledoc.browser.styling.Style;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiElement extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("cursor", "text");
    }

    public LiElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "li", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = getPanel();
        String bulletText = style.getBulletText();
        TextElement bullet = new TextElement(page, bulletText);
        new SDMLStyler().style(bullet, style);
        children.add(0, bullet);
        style(panel);
        parentPanel.add(panel);
        renderChildren(page, panel);
    }
}
