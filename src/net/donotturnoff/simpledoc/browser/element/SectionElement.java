package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class SectionElement extends BoxElement {
    public SectionElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "section", attributes, children);
    }
}
