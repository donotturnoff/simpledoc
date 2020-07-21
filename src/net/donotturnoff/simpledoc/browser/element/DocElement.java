package net.donotturnoff.simpledoc.browser.element;

import java.util.List;
import java.util.Map;

public class DocElement extends InvisibleElement {
    public DocElement(Map<String, String> attributes, List<Element> children) {
        super("doc", attributes, children);
    }
}
