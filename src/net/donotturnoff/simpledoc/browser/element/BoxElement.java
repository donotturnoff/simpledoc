package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public abstract class BoxElement extends Element {

    protected JPanel panel;

    public BoxElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
        style.setDefault("cursor", "default");
        style.setDefault("background_colour", "#FFFFFF");
    }

    protected boolean isHidden() {
        return attributes.containsKey("hidden") && !attributes.get("hidden").equals("false");
    }

    public JPanel getPanel() {
        if (isHidden()) {
            return null;
        } else {
            return new JPanel() {
                @Override
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }
            };
        }
    }

    public void style(JPanel panel) {
        if (panel != null) {
            LayoutManager layout = style.getLayoutManager(panel);
            Cursor cursor = style.getCursor();
            Color backgroundColour = style.getBackgroundColour();
            Border padding = style.getPadding();
            Border border = style.getBorder();
            Border margin = style.getMargin();
            CompoundBorder surroundings = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(margin, border), padding);

            panel.setLayout(layout);
            panel.setCursor(cursor);
            panel.setBackground(backgroundColour);
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.setAlignmentY(Component.TOP_ALIGNMENT);
            panel.setBorder(surroundings);

            panel.setToolTipText(attributes.get("title"));
        }
    }

    public void renderChildren(Page page, JPanel panel) {
        if (panel != null) {
            for (Element c : children) {
                c.render(page, panel);
            }
        }
    }

    public void refreshChildren(Page page) {
        if (panel != null) {
            for (Element c: children) {
                c.refresh(page);
            }
        }
    }

    public void addPanel(JPanel parentPanel, JPanel panel) {
        if (panel != null) {
            parentPanel.add(panel);
        }
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
        if (panel != null) {
            panel.revalidate();
            panel.repaint();
        }
        refreshChildren(page);
    }
}
