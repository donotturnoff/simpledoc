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
            loadingFailure("Failed to load resource: " + e.getMessage());
        }
    }

    @Override
    public void refresh(Page page) {

    }

    private void loadingFailure(String s) {
        page.setStatus("Failed to load " + url);
    }

    private void load() {
        page.setStatus("Loading " + url);
        ConnectionWorker worker = new ConnectionWorker(url, page, this::loaded);
        worker.execute();
    }

    public Void loaded(URL url, Response response) {
        if (response.getStatus().equals(Status.OK)) {
            String rel = attributes.getOrDefault("rel", "stylesheet");
            if (rel.equals("stylesheet")) {
                handleStylesheet(response);
            }
        } else {
            loadingFailure("Failed to load resource: resource not found");
        }

        return null;
    }

    private void handleStylesheet(Response response) {
        String body = new String(response.getBody());
        StyleWorker worker = new StyleWorker(page, page.getRoot(), body);
        worker.execute();
    }
}
