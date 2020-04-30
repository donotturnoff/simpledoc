package net.donotturnoff.simpledoc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConnectionUtils {
    public static String recv(BufferedReader in ) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        int length = 0;
        while ((line = in.readLine()) != null && !line.isBlank()) {
            if (line.trim().startsWith("length")) {
                try {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        length = Integer.parseInt(parts[1].trim());
                        if (length < 0) {
                            throw new NumberFormatException();
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    length = 0;
                }
            }
            sb.append(line);
            sb.append("\r\n");
        }
        sb.append("\r\n");
        char[] bodyText = new char[length];
        int read = in.read(bodyText, 0, length);
        sb.append(new String(bodyText));
        return sb.toString();
    }

    public static void send(PrintWriter out, String response) {
        out.write(response);
        out.flush();
    }
}
