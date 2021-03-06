package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import java.util.List;
import java.util.Map;

public class DivElement extends VisibleElement {
    public DivElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "div", attributes, children);
        setDefault("layout", "vbox");
    }
}
