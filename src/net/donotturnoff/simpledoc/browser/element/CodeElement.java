package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdml.SDMLException;

import java.util.List;
import java.util.Map;

public class CodeElement extends VisibleElement {

    public CodeElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "code", attributes, children);
        setDefault("cursor", "text");
        setDefault("font_family", "Monospaced");
    }
}
