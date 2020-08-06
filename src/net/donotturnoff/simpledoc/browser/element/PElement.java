package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class PElement extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("cursor", "text");
    }

    public PElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "p", attributes, children);
    }
}
