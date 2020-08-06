package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class BoxElement extends Element {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("cursor", "default");
        defaultStyle.set("background_color", "#FFFFFF");
    }

    public BoxElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
    }

    public JPanel getPanel() {
        return new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
    }

    public void style(JComponent component) {
        LayoutManager layout = style.getLayoutManager(component);
        Cursor cursor = style.getCursor();
        Color backgroundColour = style.getBackgroundColour();
        Border padding = style.getPadding();
        Border border = style.getBorder();
        Border margin = style.getMargin();
        CompoundBorder surroundings = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(margin, border), padding);

        component.setLayout(layout);
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
        JPanel panel = getPanel();
        style(panel);
        parentPanel.add(panel);
        renderChildren(page, panel);
    }
}
