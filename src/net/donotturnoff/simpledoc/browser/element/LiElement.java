package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class LiElement extends BoxElement {
    private TextElement bullet;
    public LiElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "li", attributes, children);
        style.setDefault("cursor", "text");

        String bulletText = style.getBulletText();
        bullet = new TextElement(page, bulletText);
        bullet.style = new Style(style);
        children.add(0, bullet);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        style(panel);
        addPanel(parentPanel, panel);
        renderChildren(page, panel);
    }

    @Override
    public void refresh(Page page) {
        style(panel);
        String bulletText = style.getBulletText();
        bullet.setText(bulletText);
        bullet.style = new Style(style);

        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
        refreshChildren(page);
    }
}
