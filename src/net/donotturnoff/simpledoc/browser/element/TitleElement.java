package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TitleElement extends Element {

    private final String text;

    public TitleElement(Page page, Map<String, String> attributes, List<Element> children) throws SDMLException {
        super(page, "title", attributes, children);
        Element c;
        if (children.size() != 1 || !((c = children.get(0)) instanceof TextElement)) {
            throw new SDMLException("Title element must contain a single text element as its child");
        }
        this.text = ((TextElement) c).getText();
    }

    @Override
    public void render(JPanel parentPanel) {
        page.setTitle(text);
    }

    @Override
    public void refresh() {

    }
}
