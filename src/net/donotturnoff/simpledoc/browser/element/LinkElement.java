package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class LinkElement extends Element {
    public LinkElement(Map<String, String> attributes, List<Element> children) {
        super("link", attributes, children);
    }

    @Override
    protected void draw(Page page) {

    }
}
