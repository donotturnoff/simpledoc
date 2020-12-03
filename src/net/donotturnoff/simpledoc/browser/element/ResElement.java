package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.*;
import net.donotturnoff.simpledoc.browser.parsing.StyleSource;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.simpledoc.util.Status;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ResElement extends Element {
    private URL url;
    private Response response;
    private final int index;

    public ResElement(Page page, Map<String, String> attributes, List<Element> children, int index) {
        super(page,"res", attributes, children);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        String src = attributes.get("src");
        try {
            url = ConnectionUtils.getURL(page.getUrl(), src);
            load();
        } catch (MalformedURLException e) {
            loadingFailure("Failed to load " + src, e);
        }
    }

    @Override
    public void refresh(Page page) {

    }

    public void loadingFailure(String s, Exception e) {
        page.removePendingResource(url, response);
        page.warning(s + ": " + e.getMessage());
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
            String rel = attributes.getOrDefault("rel", "stylesheet");
            page.info("Loaded " + url + ": " + response.getStatus());
            if (rel.equals("stylesheet")) {
                handleStylesheet();
            } else if (rel.equals("favicon")) {
                handleFavicon();
            }
        } else {
            loadingFailure("Failed to load " + url, new SDTPException(s));
        }
    }

    private void handleStylesheet() {
        String body = new String(response.getBody());
        StyleWorker worker = new StyleWorker(page, page.getRoot(), body, url, response, StyleSource.EXTERNAL, index);
        worker.execute();
    }

    private void handleFavicon() {
        byte[] body = response.getBody();
        ImageIcon favicon = new ImageIcon(body);
        page.offerFavicon(favicon, response.getHeaders().getOrDefault("type", "image/png").endsWith("gif"));
        page.removePendingResource(url, response);
    }
}
