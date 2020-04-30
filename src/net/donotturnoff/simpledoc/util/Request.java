package net.donotturnoff.simpledoc.util;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private RequestMethod method;
    private String path;
    private String protocol;
    private Map<String, String> headers;
    private String body;


    public Request(String s) throws RequestHandlingException {
        parse(s);
    }

    public Request(RequestMethod method, String path, String protocol, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void parse(String s) throws RequestHandlingException {
        if (s.isBlank()) {
            throw new RequestHandlingException(Status.BAD_REQUEST, "Request cannot be blank");
        }

        String[] lines = s.split("\\r\\n");

        // Extract first line parameters
        String firstLine = lines[0];
        if (!firstLine.matches("(GET|HEAD)(\\s+)(/\\S*)(\\s+)(SDTP/\\d+\\.\\d+)(\\s*)")) {
            throw new RequestHandlingException(Status.BAD_REQUEST, "First line of request must be of format [method] [path] [protocol]");
        }
        String[] firstLineParts = firstLine.split("\\s+");
        try {
            this.method = RequestMethod.valueOf(firstLineParts[0]);
        } catch (IllegalArgumentException e) {
            throw new RequestHandlingException(Status.BAD_REQUEST, "Invalid request method used");
        }
        this.path = firstLineParts[1];
        this.protocol = firstLineParts[2];

        // Extract headers
        int i = 1;
        this.headers = new HashMap<>();
        for (; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                break;
            }
            String[] parts = lines[i].split("=", 2);
            if (parts.length != 2) {
                throw new RequestHandlingException(Status.BAD_REQUEST, "Invalid header syntax");
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            headers.put(key, value);
        }

        // Extract body
        StringBuilder sb = new StringBuilder();
        for (; i < lines.length; i++) {
            sb.append(lines[i]);
        }
        this.body = sb.toString();
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append(" ");
        sb.append(path);
        sb.append(" ");
        sb.append(protocol);
        sb.append("\r\n");
        for (String k: headers.keySet()) {
            String v = headers.get(k);
            sb.append(k);
            sb.append("=");
            sb.append(v);
            sb.append("\r\n");
        }
        sb.append("\r\n");
        sb.append(body);
        return sb.toString();
    }
}
