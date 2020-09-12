package net.donotturnoff.simpledoc.browser;

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
    }

    @Override
    protected Void doInBackground() {
        root.render(page, panel);
        return null;
    }

    @Override
    protected void done() {
        root.cascadeStyles();
        root.refresh(page);
    }
}
