package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TitleElement extends Element {

    public TitleElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "title", attributes, children);
    }

    // TODO: fail if title element contains something other than a single text child
    @Override
    public void render(Page page, JPanel parentPanel) {
        for (Element c: children) {
            if (c instanceof TextElement) {
                page.setTitle(((TextElement) c).getText());
            }
        }
    }

    @Override
    public void refresh(Page page) {

    }
}
