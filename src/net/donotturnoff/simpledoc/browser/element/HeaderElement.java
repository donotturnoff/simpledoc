package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import java.util.List;
import java.util.Map;

public class HeaderElement extends VisibleElement {
    public HeaderElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "header", attributes, children);
    }
}