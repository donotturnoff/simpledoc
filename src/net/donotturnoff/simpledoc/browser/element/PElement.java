package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PElement extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("font_family", Font.SANS_SERIF);
        defaultStyle.put("font_size", "12");
        defaultStyle.put("font_style", "plain");
        defaultStyle.put("cursor", "text");
    }

    public PElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "p", attributes, children);
    }
}
