package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public abstract class InvisibleElement extends Element {
    public InvisibleElement(String name, Map<String, String> attributes, List<Element> children) {
        super(name, attributes, children);
    }

    @Override
    protected final void draw(Page page) {

    }
}
