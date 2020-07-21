package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class ImgElement extends Element {
    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {

    }
}
