package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H1Element extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("font_family", Font.SERIF);
        defaultStyle.put("font_size", "40");
        defaultStyle.put("font_style", "bold");
        defaultStyle.put("cursor", "text");
    }

    public H1Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h1", attributes, children);
    }
}
