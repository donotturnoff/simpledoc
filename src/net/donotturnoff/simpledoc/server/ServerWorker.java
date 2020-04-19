package net.donotturnoff.simpledoc.server;

import java.io.*;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(SDTPServer.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("log%u.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open log file", e);
        }
    }

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
        try {
            c.shutdownInput();
            c.shutdownOutput();
            c.close();
            logger.log(Level.FINE, "Halted server worker");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to gracefully halt server worker", e);
        }
    }
}
