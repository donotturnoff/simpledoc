package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BoxElement extends Element {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("cursor", "default");
        defaultStyle.put("background_color", "#FFFFFF");
    }
    public BoxElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
    }

    public JPanel createPanel() {
        return new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
    }

    public void style(JComponent component) {
        Map<String, String> style = getStyle();
        Cursor cursor = new Cursor(Element.cursorMap.getOrDefault(style.getOrDefault("cursor", "default"), Cursor.DEFAULT_CURSOR));
        Color backgroundColour = Color.decode(style.getOrDefault("background_colour", "#FFFFFF"));

        int paddingTop = Integer.parseInt(style.getOrDefault("padding_top", "0"));
        int paddingLeft = Integer.parseInt(style.getOrDefault("padding_left", "0"));
        int paddingBottom = Integer.parseInt(style.getOrDefault("padding_bottom", "0"));
        int paddingRight = Integer.parseInt(style.getOrDefault("padding_right", "0"));
        Border padding = BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight);

        int borderTopWidth = Integer.parseInt(style.getOrDefault("border_top_width", "0"));
        int borderLeftWidth = Integer.parseInt(style.getOrDefault("border_left_width", "0"));
        int borderBottomWidth = Integer.parseInt(style.getOrDefault("border_bottom_width", "0"));
        int borderRightWidth = Integer.parseInt(style.getOrDefault("border_right_width", "0"));
        Color borderColour = Color.decode(style.getOrDefault("border_colour", "#000000"));
        Border border = BorderFactory.createMatteBorder(borderTopWidth, borderLeftWidth, borderBottomWidth, borderRightWidth, borderColour);

        int marginTop = Integer.parseInt(style.getOrDefault("margin_top", "0"));
        int marginLeft = Integer.parseInt(style.getOrDefault("margin_left", "0"));
        int marginBottom = Integer.parseInt(style.getOrDefault("margin_bottom", "0"));
        int marginRight = Integer.parseInt(style.getOrDefault("margin_right", "0"));
        Border margin = BorderFactory.createEmptyBorder(marginTop, marginLeft, marginBottom, marginRight);

        CompoundBorder surroundings = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(margin, border), padding);

        component.setCursor(cursor);
        component.setBackground(backgroundColour);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentY(Component.TOP_ALIGNMENT);
        component.setBorder(surroundings);
    }

    public void renderChildren(Page page, JPanel panel) {
        for (Element c: children) {
            c.render(page, panel);
        }
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = createPanel();
        style(panel);
        parentPanel.add(panel);
        renderChildren(page, panel);
    }
}
