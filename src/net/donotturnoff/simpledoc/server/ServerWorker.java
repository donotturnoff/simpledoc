package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Request;
import net.donotturnoff.simpledoc.util.RequestHandlingException;
import net.donotturnoff.simpledoc.util.Response;

import java.io.*;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class ServerWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(SDTPServer.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open log file", e);
        }
    }

    private SDTPServer server;
    private Socket c;
    private BufferedReader in;
    private PrintWriter out;

    ServerWorker(SDTPServer server, Socket c) throws IOException {
        this.server = server;
        this.c = c;
        this.in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));
    }

    @Override
    public void run() {
        try {
            String reqString = ConnectionUtils.recv(in);
            Response response;
            try {
                Request r = new Request(reqString);
                response = RequestHandler.handle(r);
            } catch (RequestHandlingException e) {
                response = ErrorHandler.handle(e);
            }
            ConnectionUtils.send(out, response.toString());
        } catch (IOException e) {
            logger.log(Level.FINE, "Failed to read request from " + c, e);
        } finally {
            halt();
        }
    }

    void halt() {
        try {
            c.shutdownInput();
            c.shutdownOutput();
            c.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to gracefully close connection: " + c, e);
        } finally {
            server.removeWorker(this);
            logger.log(Level.FINE, "Closed connection: " + c);
        }
    }
}
