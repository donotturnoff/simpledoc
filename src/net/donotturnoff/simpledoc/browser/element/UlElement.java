package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import java.util.List;
import java.util.Map;

public class UlElement extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("bullet_style", "default");
        defaultStyle.set("margin_left", "10");
        defaultStyle.set("layout", "vbox");
    }

    public UlElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "ul", attributes, children);
    }
}
