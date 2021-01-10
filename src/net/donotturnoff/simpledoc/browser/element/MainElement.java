package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class MainElement extends VisibleElement {
    public MainElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "main", attributes, children);
    }
}
