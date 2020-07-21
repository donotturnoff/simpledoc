package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TitleElement extends Element {

    public TitleElement(Map<String, String> attributes, List<Element> children) {
        super("title", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {

    }
}
