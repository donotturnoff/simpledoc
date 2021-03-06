package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.common.*;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class ServerWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(SDTPServer.class.getName());

    private final SDTPServer server;
    private final Socket c;
    private final InputStream in;
    private final OutputStream out;

    ServerWorker(SDTPServer server, Socket c) throws IOException {
        this.server = server;
        this.c = c;
        this.in = c.getInputStream();
        this.out = c.getOutputStream();
    }

    @Override
    public void run() {
        try {
            Message msg = ConnectionUtils.recv(in);
            Response response;
            try {
                Request r = new Request(msg);
                response = RequestHandler.handle(r);
            } catch (RequestHandlingException e) {
                response = ErrorHandler.handle(e);
            }
            ConnectionUtils.send(out, new Message(response));
        } catch (IOException e) {
            logger.log(Level.INFO, "Failed to read request from " + c, e);
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
