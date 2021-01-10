package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.common.ConnectionUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class LinkElement extends VisibleElement implements MouseListener {
    public LinkElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "link", attributes, children);
        setDefault("cursor", "pointer");
        setDefault("colour", "#0000FF");
        setDefault("underline", "single");
    }

    // Navigate on click
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        super.mouseClicked(mouseEvent);
        String href = attributes.get("href");
        if (href != null) {
            page.navigate(href, false);
        }
    }

    // Update status bar on mouseover, as well as usual mouseover operations
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

    // Clear status bar on mouseout, as well as usual mouseout operations
    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        super.mouseExited(mouseEvent);
        page.setStatus(" ");
    }
}
