package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.*;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class ConnectionWorker extends SwingWorker<Response, Void> {

    private final Page page;
    private final URL url;
    private final BiFunction<URL, Response, Void> callback;
    private Exception e;
    private BiFunction<String, Exception, Void> errorHandlerCallback;

    ConnectionWorker(Page page) {
        this.url = page.getUrl();
        this.page = page;
        this.callback = page::loaded;
        this.errorHandlerCallback = page::errorHandler;
        page.addWorker(this);
    }

    ConnectionWorker(URL url, Page page) {
        this.url = url;
        this.page = page;
        this.callback = page::loaded;
        this.errorHandlerCallback = page::errorHandler;
    }

    public ConnectionWorker(URL url, Page page, BiFunction<URL, Response, Void> callback, BiFunction<String, Exception, Void> errorHandlerCallback) {
        this.url = url;
        this.page = page;
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
    }

    @Override
    protected Response doInBackground() {
        page.info("Loading " + url);
        String scheme = url.getProtocol();
        String path = url.getPath();
        if (path.isBlank()) {
            path = "/";
        }
        if (scheme.equals("sdtp")) {
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
        } else if (scheme.equals("file")) {
            try {
                Path p = Paths.get(url.getPath());
                byte[] data = Files.readAllBytes(p);
                Map<String, String> headers = new HashMap<>();
                String mime = FileUtils.getMime(p);
                mime = (mime == null) ? "text/plain" : mime; // TODO: make customiseable
                headers.put("type", mime);
                return new Response("file", Status.OK, headers, data);
            } catch (IOException e) {
                this.e = e;
                return null;
            }
        } else {
            this.e = new MalformedURLException("Illegal scheme: " + scheme);
            return null;
        }
    }

    @Override
    public void done() {
        try {
            Response response = get();
            if (response != null) {
                page.addResource(url, response);
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
            page.addResource(url, null);
            errorHandlerCallback.apply("Failed to load " + url, e);
        }
    }
}
