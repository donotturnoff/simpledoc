package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import java.util.List;
import java.util.Map;

public class SectionElement extends VisibleElement {
    public SectionElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "section", attributes, children);
    }
}
