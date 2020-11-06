package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H1Element extends BoxElement {
    public H1Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h1", attributes, children);
        setDefault("font_family", Font.SERIF);
        setDefault("font_size", "32");
        setDefault("font_style", "bold");
        setDefault("cursor", "text");
        setDefault("margin_top", "5");
        setDefault("margin_bottom", "5");
    }
}
