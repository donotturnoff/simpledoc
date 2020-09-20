package net.donotturnoff.simpledoc.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionUtils {
    public static Message recv(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        int bodyLength = 0;
        while (!(line = readLine(in)).isBlank()) {
            if (line.trim().startsWith("length")) {
                try {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        bodyLength = Integer.parseInt(parts[1].trim());
                        if (bodyLength < 0) {
                            throw new NumberFormatException();
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    bodyLength = 0;
                }
            }
            sb.append(line);
            sb.append("\r\n");
        }
        sb.append("\r\n");
        byte[] body = new byte[bodyLength];
        //int read = in.read(body, 0, bodyLength);
        int read = read(in, body, 0, bodyLength);
        return new Message(sb.toString(), body);
    }

    /*
     * Inspired by InputStream.read(byte[] b, int off, int len)
     * For some reason, it works as a static method but truncates the input as a non-static method
     */
    public static int read(InputStream in, byte[] data, int start, int len) throws IOException {
        if (len == 0) {
            return 0;
        } else {
            int c = in.read();
            if (c == -1) {
                return -1;
            } else {
                data[start] = (byte) c;
                int i = 1;
                try {
                    for (; i < len; i++) {
                        c = in.read();
                        if (c == -1) {
                            break;
                        }

                        data[start+i] = (byte) c;
                    }
                } catch (IOException ignored) {
                }

                return i;
            }
        }
    }

    private static String readLine(InputStream in) throws IOException {
        StringBuilder line = new StringBuilder();
        byte prev = 0, c;
        while ((c = (byte) in.read()) >= 0 && !(prev == '\r' && c == '\n')) {
            prev = c;
            line.append((char) c);
        }
        return line.substring(0, line.length() - 1);
    }

    public static void send(OutputStream out, Message msg) throws IOException {
        out.write(msg.getHead().getBytes());
        out.write(msg.getBody());
        out.flush();
    }

    public static URL getURL(URL current, String s) throws MalformedURLException {
        URL url;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            url = new URL(current, s);
        }
        String scheme = url.getProtocol();
        if (!(scheme.equals("sdtp") || scheme.equals("file"))) {
            throw new MalformedURLException("Scheme must be sdtp");
        }
        return url;
    }
}
