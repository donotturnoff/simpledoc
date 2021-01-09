package net.donotturnoff.simpledoc.browser.sdtp;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class SDTPURLConnection extends URLConnection {

    private final static int DEFAULT_PORT = 5000; // TODO: make configurable

    private InputStream in;
    private OutputStream out;

    SDTPURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        int port;
        if ((port = url.getPort()) == -1) {
            port = DEFAULT_PORT;
        }
        Socket s = new Socket(url.getHost(), port);
        in = s.getInputStream();
        out = s.getOutputStream();
        connected = true;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }
}
