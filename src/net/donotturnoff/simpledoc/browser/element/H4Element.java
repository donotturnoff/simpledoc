package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H4Element extends BoxElement {
    public H4Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h4", attributes, children);
        style.setDefault("font_family", Font.SERIF);
        style.setDefault("font_size", "16");
        style.setDefault("font_style", "bold");
        style.setDefault("cursor", "text");
        style.setDefault("margin_top", "3");
        style.setDefault("margin_bottom", "3");
    }
}
