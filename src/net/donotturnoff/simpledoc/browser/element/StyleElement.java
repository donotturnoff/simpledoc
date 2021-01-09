package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.sdss.StyleWorker;
import net.donotturnoff.simpledoc.browser.sdss.StyleSource;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class StyleElement extends Element {

    private final int index;

    public StyleElement(Page page, Map<String, String> attributes, List<Element> children, int index) {
        super(page,"style", attributes, children);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        for (Element child: children) {
            if (child instanceof TextElement) {
                String body = ((TextElement) child).getText();
                StyleWorker worker = new StyleWorker(page, page.getRoot(), body, null, null, StyleSource.INTERNAL, index);
                worker.execute();
            }
        }
    }
}
