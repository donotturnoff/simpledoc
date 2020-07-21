package net.donotturnoff.simpledoc.browser.element;

import java.util.List;
import java.util.Map;

public class BodyElement extends InvisibleElement {
    public BodyElement(Map<String, String> attributes, List<Element> children) {
        super("body", attributes, children);
    }
}
