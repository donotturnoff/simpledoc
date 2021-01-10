package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.*;
import net.donotturnoff.simpledoc.browser.components.JImagePanel;
import net.donotturnoff.simpledoc.browser.sdtp.ConnectionWorker;
import net.donotturnoff.simpledoc.browser.sdtp.SDTPException;
import net.donotturnoff.simpledoc.common.ConnectionUtils;
import net.donotturnoff.simpledoc.common.Response;
import net.donotturnoff.simpledoc.common.Status;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ImgElement extends VisibleElement {

    private URL url;
    private Response response;
    private boolean success = true;

    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    public JImagePanel getPanel() {
        JImagePanel panel = new JImagePanel(this::rendered, this::loadingFailure);
        panel.addMouseListener(this);
        return panel;
    }

    @Override
    public void render(JPanel parentPanel) {
        panel = getPanel();
        style();

        // Add image panel to parent
        parentPanel.add(panel);

        String src = attributes.get("src");
        try {
            url = ConnectionUtils.getURL(page.getUrl(), src);
            load();
        } catch (MalformedURLException e) {
            loadingFailure("Failed to load " + src, e);
        }
    }

    @Override
    public void refresh() {
        style();
        panel.revalidate();
        panel.repaint();
        if (!success) {
            refreshChildren();
        }
    }

    private void loadingFailure(String msg, Exception e) {
        success = false;
        page.removePendingResource(url, response);

        // Render alternative elements on image load failure
        renderChildren(panel);

        page.warning(msg + ": " + e.getMessage());
        panel.revalidate();
        panel.repaint();
    }

    public void loadingFailure(Exception e) {
        loadingFailure("Failed to load " + url, e);
    }

    private void load() {
        page.addPendingResource(url);
        ConnectionWorker worker = new ConnectionWorker(url, page, this::loaded, this::loadingFailure);
        worker.execute();
    }

    public void loaded(URL url, Response response) {
        this.response = response;
        Status s = response.getStatus();
        if (s == Status.OK) {
            // Start image rendering
            byte[] data = response.getBody();
            ((JImagePanel) panel).setImage(data);
        } else {
            loadingFailure("Failed to load " + url, new SDTPException(s));
        }
    }

    // Called when image is drawn on the page
    public void rendered() {
        page.info("Loaded " + url + ": " + response.getStatus());
        page.removePendingResource(url, response);
    }
}
