package net.donotturnoff.simpledoc.server;

import java.util.HashMap;
import java.util.Map;

class Request {

    private RequestMethod method;
    private String path;
    private String protocol;
    private Map<String, String> headers;
    private String body;

    Request(String reqString) throws RequestHandlingException {
        parse(reqString);
    }

    RequestMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getProtocol() {
        return protocol;
    }

    Map<String, String> getHeaders() {
        return headers;
    }

    String getBody() {
        return body;
    }

    private void parse(String requestString) throws RequestHandlingException {
        if (requestString.isBlank()) {
            throw new RequestHandlingException("Request cannot be blank");
        }

        String[] lines = requestString.split("\\r\\n");

        // Extract first line parameters
        String firstLine = lines[0];
        if (!firstLine.matches("(GET|HEAD)(\\s+)(/\\S*)(\\s+)(SDTP/\\d+\\.\\d+)(\\s*)")) {
            throw new RequestHandlingException("First line of request must be of format [method] [path] [protocol]");
        }
        String[] firstLineParts = firstLine.split("\\s+");
        try {
            this.method = RequestMethod.valueOf(firstLineParts[0]);
        } catch (IllegalArgumentException e) {
            throw new RequestHandlingException("Invalid request method used");
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
            String[] parts = lines[i].split("=", 1);
            if (parts.length != 2) {
                throw new RequestHandlingException("Invalid header syntax");
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
}
