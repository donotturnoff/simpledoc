package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.*;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.function.BiFunction;

public class ConnectionWorker extends SwingWorker<Response, Void> {

    private final Page page;
    private final URL url;
    private final BiFunction<URL, Response, Void> callback;
    private Exception e;

    ConnectionWorker(Page page) {
        this.url = page.getUrl();
        this.page = page;
        this.callback = page::loaded;
        page.addWorker(this);
    }

    ConnectionWorker(URL url, Page page) {
        this.url = url;
        this.page = page;
        this.callback = page::loaded;
    }

    public ConnectionWorker(URL url, Page page, BiFunction<URL, Response, Void> callback) {
        this.url = url;
        this.page = page;
        this.callback = callback;
    }

    @Override
    protected Response doInBackground() {
        page.info("Loading " + url);
        String path = url.getPath();
        if (path.isBlank()) {
            path = "/";
        }

        try {
            URLConnection c = url.openConnection();
            c.setDoOutput(true);
            c.connect();
            InputStream in = c.getInputStream();
            OutputStream out = c.getOutputStream();

            Request request = new Request(RequestMethod.GET, path, "SDTP/0.1", Map.of(), new byte[0]);
            ConnectionUtils.send(out, new Message(request));
            return new Response(ConnectionUtils.recv(in));
        } catch (Exception e) {
            this.e = e;
            return null;
        }
    }

    @Override
    public void done() {
        try {
            Response response = get();
            if (response != null) {
                Status s = response.getStatus();
                if (s.equals(Status.OK)) {
                    page.info("Loaded " + url + ": " + s);
                } else {
                    page.error("Failed to load " + url + ": " + s);
                }
                callback.apply(url, response);
            } else {
                throw e;
            }
        } catch (Exception e) {
            page.error("Failed to load " + url, e);
        }
    }
}
