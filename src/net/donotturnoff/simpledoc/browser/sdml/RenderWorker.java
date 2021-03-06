package net.donotturnoff.simpledoc.browser.sdml;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.element.Element;

import javax.swing.*;

public class RenderWorker extends SwingWorker<Void, Void> {

    private final Page page;
    private final Element root;
    private final JPanel panel;

    public RenderWorker(Page page, Element root) {
        this.page = page;
        this.root = root;
        this.panel = page.getPanel();
        page.addWorker(this);
    }

    @Override
    protected Void doInBackground() {
        page.info("Rendering " + page.getUrl());
        root.render(panel);
        return null;
    }

    @Override
    protected void done() {
        root.cascadeStyles(); // Pass inheritable styles down to children once all elements have their own style
        root.refresh();
        page.removePendingResource(page.getUrl(), page.getData(), 0);
        page.info("Rendered " + page.getUrl());
    }
}
