package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkElement extends BoxElement implements MouseListener {
    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("cursor", "pointer");
        defaultStyle.put("colour", "#0000FF");
        defaultStyle.put("text_decoration", "underline");
    }

    public LinkElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "link", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        super.render(page, parentPanel);
        parentPanel.addMouseListener(this);
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
