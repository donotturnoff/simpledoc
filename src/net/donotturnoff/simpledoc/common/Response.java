package net.donotturnoff.simpledoc.common;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private String protocol;
    private Status status;
    private HashMap<String, String> headers;
    private final byte[] body;

    public Response(Message msg) throws ResponseHandlingException {
        parse(msg.getHead());
        this.body = msg.getBody();
    }

    public Response(String protocol, Status status, Map<String, String> headers, byte[] body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = new HashMap<>();
        this.headers.putAll(headers);
        this.body = body;
    }

    public void putHeader(String k, String v) {
        headers.put(k, v);
    }

    public String getProtocol() {
        return protocol;
    }

    public Status getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    private void parse(String s) throws ResponseHandlingException {
        if (s.isBlank()) {
            throw new ResponseHandlingException("Response cannot be blank");
        }

        String[] lines = s.split("\\r\\n");

        // Extract first line parameters
        String firstLine = lines[0];
        if (!firstLine.matches("(SDTP/\\d+\\.\\d+)(\\s+)(\\d\\d\\d)(\\s+)(\\w+)(.*)")) {
            throw new ResponseHandlingException("First line of response must be of format [protocol] [code] [status]");
        }
        String[] firstLineParts = firstLine.split("\\s+");
        this.protocol = firstLineParts[0];
        try {
            int code = Integer.parseInt(firstLineParts[1]);
            Status status = Status.getByCode(code);
            if (status == null) {
                throw new ResponseHandlingException("Invalid status code received");
            }
            this.status = status;
        } catch (IllegalArgumentException e) {
            throw new ResponseHandlingException("Invalid status code received");
        }

        // Extract headers
        int i = 1;
        this.headers = new HashMap<>();
        for (; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                break;
            }
            String[] parts = lines[i].split("=", 2);
            if (parts.length != 2) {
                throw new ResponseHandlingException("Invalid header syntax");
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            headers.put(key, value);
        }
    }

    public String getHead() {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol);
        sb.append(" ");
        sb.append(status);
        sb.append("\r\n");
        for (String k: headers.keySet()) {
            String v = headers.get(k);
            sb.append(k);
            sb.append("=");
            sb.append(v);
            sb.append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return getHead() + new String(body);
    }
}
