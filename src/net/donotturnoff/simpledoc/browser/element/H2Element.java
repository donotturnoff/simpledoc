package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H2Element extends BoxElement {
    public H2Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h2", attributes, children);
        setDefault("font_family", Font.SERIF);
        setDefault("font_size", "24");
        setDefault("font_style", "bold");
        setDefault("cursor", "text");
        setDefault("margin_top", "4");
        setDefault("margin_bottom", "4");
    }
}
