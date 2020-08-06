package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

public class LinkElement extends BoxElement implements MouseListener {
    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("cursor", "pointer");
        defaultStyle.set("colour", "#0000FF");
        defaultStyle.set("underline", "single");
    }

    public LinkElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "link", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        JPanel panel = getPanel();
        style(panel);
        panel.addMouseListener(this);
        parentPanel.add(panel);
        renderChildren(page, panel);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        String href = attributes.get("href");
        if (href != null) {
            page.navigate(href);
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
