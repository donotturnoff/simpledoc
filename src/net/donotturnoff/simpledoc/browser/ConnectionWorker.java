package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.*;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
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
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            port = 5000; //TODO: add to config file
        }
        String path = url.getPath();

        Socket s = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));

        Request request = new Request(RequestMethod.GET, path, "SDTP/0.1", Map.of(), "");
        ConnectionUtils.send(out, request.toString());
        return new Response(ConnectionUtils.recv(in));
    }

    @Override
    public void done() {
        try {
            Response response = get();
            //TODO: call renderer
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
