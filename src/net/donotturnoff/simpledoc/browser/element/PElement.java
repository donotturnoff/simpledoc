package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class PElement extends Element {
    public PElement(Map<String, String> attributes, List<Element> children) {
        super("p", attributes, children);
    }

    @Override
    protected void draw(Page page) {

    }
}
