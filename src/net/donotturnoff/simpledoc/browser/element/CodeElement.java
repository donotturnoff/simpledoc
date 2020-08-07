package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import java.util.List;
import java.util.Map;

public class CodeElement extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("cursor", "text");
        defaultStyle.set("font_family", "Monospaced");
    }

    public CodeElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "code", attributes, children);
    }
}
