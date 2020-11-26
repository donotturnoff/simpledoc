package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class DivElement extends BoxElement {
    public DivElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "div", attributes, children);
        setDefault("layout", "vbox");
    }
}
