package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import java.util.List;
import java.util.Map;

public class DocElement extends InvisibleElement {
    public DocElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "doc", attributes, children);
    }
}
