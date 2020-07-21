package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class DocElement extends Element {
    public DocElement(Map<String, String> attributes, List<Element> children) {
        this.name = "doc";
        this.attributes = attributes;
        this.children = children;
    }

    @Override
    protected void draw(JPanel panel) {

    }
}
