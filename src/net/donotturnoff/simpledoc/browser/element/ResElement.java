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

public class ResElement extends Element {
    private URL url;

    public ResElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page,"res", attributes, children);

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
        Status s = response.getStatus();
        if (s == Status.OK) {
            String rel = attributes.getOrDefault("rel", "stylesheet");
            page.info("Loaded " + url + ": " + response.getStatus());
            if (rel.equals("stylesheet")) {
                handleStylesheet(response);
            } else if (rel.equals("favicon")) {
                handleFavicon(response);
            }
        } else {
            loadingFailure("Failed to load " + url, new SDTPException(s));
        }
    }

    private void handleStylesheet(Response response) {
        String body = new String(response.getBody());
        StyleWorker worker = new StyleWorker(page, page.getRoot(), body, url, response);
        worker.execute();
    }

    private void handleFavicon(Response response) {
        byte[] body = response.getBody();
        ImageIcon favicon = new ImageIcon(body);
        page.offerFavicon(favicon, response.getHeaders().getOrDefault("type", "image/png").endsWith("gif"));
        page.removePendingResource(url, response);
    }
}
