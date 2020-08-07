package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class NavElement extends BoxElement {
    public NavElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "nav", attributes, children);
    }
}
