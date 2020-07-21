package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class H1Element extends Element {
    public H1Element(Map<String, String> attributes, List<Element> children) {
        super("h1", attributes, children);
    }

    @Override
    protected void draw(Page page) {

    }
}
