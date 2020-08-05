package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UlElement extends BoxElement {

    public final static Map<String, String> defaultStyle = new HashMap<>();

    static {
        defaultStyle.put("bullet_style", "default");
        defaultStyle.put("margin_left", "10");
        defaultStyle.put("layout", "vbox");
    }

    public UlElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "ul", attributes, children);
    }
}
