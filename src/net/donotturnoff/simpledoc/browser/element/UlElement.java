package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import java.util.List;
import java.util.Map;

public class UlElement extends BoxElement {
    public UlElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "ul", attributes, children);
        style.setDefault("bullet_style","default");
        style.setDefault("margin_left", "10");
        style.setDefault("layout", "vbox");
    }
}
