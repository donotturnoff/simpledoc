package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.Style;

import java.util.List;
import java.util.Map;

public class UlElement extends BoxElement {
    public UlElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "ul", attributes, children);
        setDefault("bullet_style","default");
        setDefault("margin_left", "10");
        setDefault("layout", "vbox");
    }
}
