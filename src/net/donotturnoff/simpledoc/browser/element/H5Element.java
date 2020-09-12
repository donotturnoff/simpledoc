package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H5Element extends BoxElement {
    public H5Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h5", attributes, children);
        style.setDefault("font_family", Font.SERIF);
        style.setDefault("font_size", "14");
        style.setDefault("font_style", "bold");
        style.setDefault("cursor", "text");
        style.setDefault("margin_top", "2");
        style.setDefault("margin_bottom", "2");
    }
}
