package net.donotturnoff.simpledoc.server;

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
            String request = recv();
            RequestHandler handler = new RequestHandler(request);
            String response = handler.handle();
            send(response);
        } catch (IOException e) {
            logger.log(Level.FINE, "Failed to read request from " + c, e);
        } finally {
            halt();
        }
    }

    private String recv() throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        int length = 0;
        while ((line = in.readLine()) != null && !line.isBlank()) {
            if (line.trim().startsWith("length")) {
                try {
                    length = Integer.parseInt(line.split("=")[1].trim());
                    if (length < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    length = 0;
                    logger.log(Level.FINER, "Invalid length header (ignoring body; error will be handled later)", e);
                }
            }
            sb.append(line);
            sb.append("\r\n");
        }
        sb.append("\r\n");
        char[] bodyText = new char[length];
        int read = in.read(bodyText, 0, length);
        sb.append(new String(bodyText));

        logger.log(Level.FINER, "Received request");
        return sb.toString();
    }

    private void send(String response) {
        out.write(response);
        out.flush();
        logger.log(Level.FINER, "Sent response");
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
