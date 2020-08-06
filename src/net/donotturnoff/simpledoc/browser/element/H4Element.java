package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.styling.Style;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class H4Element extends BoxElement {

    public final static Style defaultStyle = new Style();

    static {
        defaultStyle.set("font_family", Font.SERIF);
        defaultStyle.set("font_size", "16");
        defaultStyle.set("font_style", "bold");
        defaultStyle.set("cursor", "text");
        defaultStyle.set("margin_top", "3");
        defaultStyle.set("margin_bottom", "3");
    }

    public H4Element(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "h4", attributes, children);
    }
}
