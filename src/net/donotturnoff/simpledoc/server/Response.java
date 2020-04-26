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
