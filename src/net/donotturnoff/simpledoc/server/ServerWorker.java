package net.donotturnoff.simpledoc.server;

import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private Socket c;
    private BufferedReader in;
    private PrintWriter out;

    ServerWorker(Socket c) throws IOException {
        this.c = c;
        this.in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));
    }

    @Override
    public void run() {

    }

    void halt() {

    }
}
