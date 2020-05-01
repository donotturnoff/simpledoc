package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class ConnectionWorker extends SwingWorker<Response, Void> {

    private final URL url;
    private Socket s;
    private BufferedReader in;
    private PrintWriter out;

    ConnectionWorker(URL url) {
        this.url = url;
    }

    @Override
    protected Response doInBackground() throws IOException, ResponseHandlingException {
        String path = url.getPath();
        if (path.isBlank()) {
            path = "/";
        }

        URLConnection c = url.openConnection();
        c.setDoOutput(true);
        c.connect();
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));

        Request request = new Request(RequestMethod.GET, path, "SDTP/0.1", Map.of(), "");
        ConnectionUtils.send(out, request.toString());
        return new Response(ConnectionUtils.recv(in));
    }

    @Override
    public void done() {
        try {
            Response response = get();
            System.out.println(response);
            //TODO: call renderer
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
