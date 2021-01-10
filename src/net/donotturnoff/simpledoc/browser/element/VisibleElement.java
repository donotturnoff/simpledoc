package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

// For box-based elements which get drawn on the page
public abstract class VisibleElement extends Element {

    protected JPanel panel;

    public VisibleElement(Page page, String name, Map<String, String> attributes, List<Element> children) {
        super(page, name, attributes, children);
        setDefault("cursor", "default");
        setDefault("background_colour", "#FFFFFF");
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel() {
                @Override
                public Dimension getMaximumSize() {
                    return getPreferredSize();
                }
            };
        panel.addMouseListener(this);
        return panel;
    }

    // Apply all relevant styles
    public void style() {
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

    // Renders children for the first time
    public void renderChildren(JPanel panel) {
        for (Element c : children) {
            c.render(panel);
        }
    }

    // Refreshes children for subsequent updates (avoids reloading resources, etc.)
    public void refreshChildren() {
        for (Element c: children) {
            c.refresh();
        }
    }

    @Override
    public void render(JPanel parentPanel) {
        panel = getPanel();
        style();
        parentPanel.add(panel);
        renderChildren(panel);
    }

    @Override
    public void refresh() {
        style();
        panel.revalidate();
        panel.repaint();
        refreshChildren();
    }
}
