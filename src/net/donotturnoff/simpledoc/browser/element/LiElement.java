package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class LiElement extends BoxElement {
    private TextElement bullet;
    public LiElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "li", attributes, children);
        setDefault("cursor", "text");
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        String bulletText = getStyle().getBulletText();
        bullet = new TextElement(page, bulletText);
        children.add(0, bullet);

        panel = getPanel();
        style(panel);
        addPanel(parentPanel, panel);
        renderChildren(page, panel);
    }

    @Override
    public void refresh(Page page) {
        style(panel);
        String bulletText = getStyle().getBulletText();
        bullet.setText(bulletText);

        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
        refreshChildren(page);
    }
}
