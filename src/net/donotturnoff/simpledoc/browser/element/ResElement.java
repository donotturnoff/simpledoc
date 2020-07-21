package net.donotturnoff.simpledoc.browser.element;

import java.util.List;
import java.util.Map;

public class ResElement extends InvisibleElement {
    public ResElement(Map<String, String> attributes, List<Element> children) {
        super("res", attributes, children);
    }
}
