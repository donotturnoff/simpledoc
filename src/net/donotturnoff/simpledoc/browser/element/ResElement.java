package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class ResElement extends InvisibleElement {
    public ResElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page,"res", attributes, children);
    }
}
