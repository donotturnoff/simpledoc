package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TitleElement extends Element {

    public TitleElement(Map<String, String> attributes, List<Element> children) {
        super("title", attributes, children);
    }

    @Override
    protected void draw(JPanel panel) {

    }
}
