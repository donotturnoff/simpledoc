package net.donotturnoff.simpledoc.browser.sdtp;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.SDTPBrowser;
import net.donotturnoff.simpledoc.common.*;

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
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ConnectionWorker extends SwingWorker<Response, Void> {

    private final Page page;
    private final URL url;
    private final BiConsumer<URL, Response> callback;
    private final SDTPBrowser browser;
    private Exception e;
    private final Consumer<Exception> errorHandlerCallback;

    public ConnectionWorker(Page page) {
        this.url = page.getUrl();
        this.page = page;
        this.browser = page.getBrowser();
        this.callback = page::loaded;
        this.errorHandlerCallback = page::errorHandler;
        page.addWorker(this);
    }

    public ConnectionWorker(URL url, Page page) {
        this.url = url;
        this.page = page;
        this.browser = page.getBrowser();
        this.callback = page::loaded;
        this.errorHandlerCallback = page::errorHandler;
        page.addWorker(this);
    }

    public ConnectionWorker(URL url, Page page, BiConsumer<URL, Response> callback, Consumer<Exception> errorHandlerCallback) {
        this.url = url;
        this.page = page;
        this.browser = page.getBrowser();
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        page.addWorker(this);
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
        } else if (scheme.equals("file")) { // Local files
            // TODO: local directories
            try {
                Path p = Paths.get(url.getPath());
                byte[] data = Files.readAllBytes(p); // Read straight from file
                Map<String, String> headers = new HashMap<>();
                String mime = FileUtils.getMime(p);
                mime = (mime == null) ? browser.getConfig().getProperty("default_mime") : mime;
                headers.put("type", mime);
                return new Response("file", Status.OK, headers, data);
            } catch (IOException e) {
                this.e = e;
                return null;
            }
        } else {
            this.e = new MalformedURLException("Scheme must be sdtp or file");
            return null;
        }
    }

    @Override
    public void done() {
        Response response;
        try {
            response = get();
            if (response != null) {
                callback.accept(url, response);
            } else {
                throw e; // Throw any exception we stored in doInBackground
            }
        } catch (CancellationException e) {
            page.info("Loading of " + url + " cancelled");
        } catch (Exception e) {
            errorHandlerCallback.accept(e);
        }
    }
}
