package net.donotturnoff.simpledoc.browser.element;

import java.util.List;
import java.util.Map;

public class HeadElement extends InvisibleElement {
    public HeadElement(Map<String, String> attributes, List<Element> children) {
        super("head", attributes, children);
    }
}
