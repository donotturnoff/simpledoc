package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H6Element extends VisibleElement {
    public H6Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h6", attributes, children);
        setDefault("font_family", Font.SERIF);
        setDefault("font_size", "12");
        setDefault("font_style", "bold");
        setDefault("cursor", "text");
        setDefault("margin_top", "2");
        setDefault("margin_bottom", "2");
    }
}
