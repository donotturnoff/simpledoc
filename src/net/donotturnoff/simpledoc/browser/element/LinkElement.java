package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;
import net.donotturnoff.simpledoc.util.ConnectionUtils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class LinkElement extends BoxElement implements MouseListener {
    public LinkElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "link", attributes, children);
        setDefault("cursor", "pointer");
        setDefault("colour", "#0000FF");
        setDefault("underline", "single");
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        style(panel);
        addPanel(parentPanel, panel);
        renderChildren(page, panel);
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        String href = attributes.get("href");
        if (href != null) {
            page.navigate(href, false);
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        super.mouseEntered(mouseEvent);
        String href = attributes.get("href");
        if (href != null) {
            try {
                page.setStatus(ConnectionUtils.getURL(page.getUrl(), href).toString());
            } catch (MalformedURLException e) {
                page.setStatus(href);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        super.mouseExited(mouseEvent);
        page.setStatus(" ");
    }
}
