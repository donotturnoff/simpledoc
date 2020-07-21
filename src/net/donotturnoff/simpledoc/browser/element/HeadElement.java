package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class HeadElement extends InvisibleElement {
    public HeadElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "head", attributes, children);
    }
}
