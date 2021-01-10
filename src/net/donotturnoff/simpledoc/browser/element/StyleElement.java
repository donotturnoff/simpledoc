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

    // TODO: make use of element tree directly inside style element, rather than inside a string
    @Override
    public void render(JPanel parentPanel) {
        for (Element child: children) {
            if (child instanceof TextElement) {
                String body = ((TextElement) child).getText();

                // Delegate styling to worker
                StyleWorker worker = new StyleWorker(page, page.getRoot(), body, null, null, StyleSource.INTERNAL, index);
                worker.execute();
            }
        }
    }

    @Override
    public void refresh() {}
}
