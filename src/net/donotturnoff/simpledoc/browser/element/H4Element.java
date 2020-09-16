package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H4Element extends BoxElement {
    public H4Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h4", attributes, children);
        setDefault("font_family", Font.SERIF);
        setDefault("font_size", "16");
        setDefault("font_style", "bold");
        setDefault("cursor", "text");
        setDefault("margin_top", "3");
        setDefault("margin_bottom", "3");
    }
}
