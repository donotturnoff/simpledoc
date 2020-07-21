package net.donotturnoff.simpledoc.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SDTPServer {

    static final String DEFAULT_PROTOCOL = "SDTP/0.1";

    private static final Properties defaultConfig = new Properties();
    static Properties config;
    private static final Logger logger = Logger.getLogger(SDTPServer.class.getName());
    private final int port;
    private ServerSocket socket;
    private final Set<ServerWorker> workers;

    static {
        defaultConfig.setProperty("docroot", "/var/sdtp/sdml/");
        defaultConfig.setProperty("log", "log.txt");
        defaultConfig.setProperty("server", "SDTPServer 0.1");
        defaultConfig.setProperty("follow_symlinks", "no");
        defaultConfig.setProperty("default_mime", "text/sdml");
        defaultConfig.setProperty("index_pages", "index.sdml");
        defaultConfig.setProperty("listdir", "no");
        config = new Properties(defaultConfig);
        try {
            InputStream propStream = new FileInputStream("/etc/sdtp/server.conf");
            config.load(propStream);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to open config file");
        }

        try {
            FileHandler fileHandler = new FileHandler("log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open log file", e);
        }
    }

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                SDTPServer server = new SDTPServer(Integer.parseInt(args[0]));
                server.run();
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, "Invalid port number", e);
                System.exit(3);
            }
        } else {
            logger.log(Level.SEVERE, "One argument required: port number");
            System.exit(2);
        }
    }

    private SDTPServer(int port) throws IllegalArgumentException {
        workers = new HashSet<>();
        this.port = port;
    }

    private void run() {
        try {
            socket = new ServerSocket(port);
            Runtime.getRuntime().addShutdownHook(new Thread(this::halt));
            socket.setReuseAddress(true);
            logger.log(Level.INFO, "Listening on port " + socket.getLocalPort());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to start server", e);
            System.exit(4);
        }
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Socket c = socket.accept();
                logger.log(Level.INFO, "Accepted new connection: " + c);
                ServerWorker worker = new ServerWorker(this, c);
                workers.add(worker);
                (new Thread(worker, c.toString())).start();
                logger.log(Level.FINE, "Dispatched new server worker to handle " + c);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Failed to accept connection", e);
            }

        }
    }

    void removeWorker(ServerWorker worker) {
        workers.remove(worker);
    }

    private void halt() {
        try {
            for (ServerWorker worker: workers) {
                worker.halt();
            }
            socket.close();
            logger.log(Level.INFO, "Halted server");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to gracefully halt server", e);
        }
    }
}
