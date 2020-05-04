package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

class ConnectionWorker extends SwingWorker<Response, Void> {

    private final Page page;
    private Socket s;
    private BufferedReader in;
    private PrintWriter out;
    private Exception e;

    ConnectionWorker(Page page) {
        this.page = page;
    }

    @Override
    protected Response doInBackground() {
        URL url = page.getUrl();
        String path = url.getPath();
        if (path.isBlank()) {
            path = "/";
        }

        try {
            URLConnection c = url.openConnection();
            c.setDoOutput(true);
            c.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

            Request request = new Request(RequestMethod.GET, path, "SDTP/0.1", Map.of(), "");
            ConnectionUtils.send(out, request.toString());
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
                page.setData(response);
                page.parse();
                page.render();
            } else {
                throw e;
            }
        } catch (Exception e) {
            page.displayError(e);
        }
    }
}
