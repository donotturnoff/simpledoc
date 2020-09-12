package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class LiElement extends BoxElement {
    public LiElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "li", attributes, children);
        style.setDefault("cursor", "text");
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        String bulletText = style.getBulletText();
        TextElement bullet = new TextElement(page, bulletText);
        bullet.style = new Style(style);
        children.add(0, bullet);
        style(panel);
        addPanel(parentPanel, panel);
        renderChildren(page, panel);
    }
}
