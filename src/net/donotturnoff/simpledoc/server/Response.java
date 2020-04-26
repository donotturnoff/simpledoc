package net.donotturnoff.simpledoc.server;

import java.util.Map;

class Response {
    private String protocol;
    private Status status;
    private Map<String, String> headers;
    private String body;

    Response(String protocol, Status status, Map<String, String> headers, String body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.body = body;
        addDefaultHeaders();
    }

    String getProtocol() {
        return protocol;
    }

    Status getStatus() {
        return status;
    }

    Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    private void addDefaultHeaders() {
        headers.put("length", Integer.toString(body.length()));
        headers.put("server", SDTPServer.SERVER_NAME);
    }

    @Override
    public String toString() {
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
        sb.append(body);
        return sb.toString();
    }
}
