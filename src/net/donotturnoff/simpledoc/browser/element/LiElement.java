package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.SDMLStyler;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiElement extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("cursor", "text");
    }

    public LiElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "li", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = createPanel();
        Map<String, String> style = getStyle();
        String bulletTextName = style.getOrDefault("bullet_style", "default");
        String bulletText = bulletStyleMap.getOrDefault(bulletTextName, bulletTextName);
        TextElement bullet = new TextElement(page, bulletText);
        new SDMLStyler().style(bullet, style);
        children.add(0, bullet);
        style(panel);
        parentPanel.add(panel);
        renderChildren(page, panel);
    }
}
