package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.*;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.simpledoc.util.Status;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ImgElement extends BoxElement {

    private URL url;
    private Response response;

    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    public JImagePanel getPanel() {
        if (isHidden()) {
            return null;
        } else {
            JImagePanel panel = new JImagePanel(this::rendered, this::loadingFailure);
            panel.addMouseListener(this);
            return panel;
        }
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        style(panel);
        addPanel(parentPanel, panel);
        if (panel != null) {
            String src = attributes.get("src");
            try {
                url = ConnectionUtils.getURL(page.getUrl(), src);
                load();
            } catch (MalformedURLException e) {
                loadingFailure("Failed to load " + src, e);
            }
        }
    }

    private void loadingFailure(String msg, Exception e) {
        page.removePendingResource(url, response);
        super.renderChildren(page, panel);
        page.warning(msg + ": " + e.getMessage());
        panel.repaint();
        panel.revalidate();
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
            byte[] data = response.getBody();
            ((JImagePanel) panel).setImage(data);
        } else {
            loadingFailure("Failed to load " + url, new SDTPException(s));
        }
    }

    public void rendered() {
        page.info("Loaded " + url + ": " + response.getStatus());
        page.removePendingResource(url, response);
    }
}
