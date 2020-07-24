package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H5Element extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("font_family", Font.SERIF);
        defaultStyle.put("font_size", "14");
        defaultStyle.put("font_style", "bold");
        defaultStyle.put("cursor", "text");
        defaultStyle.put("margin_top", "2");
        defaultStyle.put("margin_bottom", "2");
    }

    public H5Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h5", attributes, children);
    }
}
