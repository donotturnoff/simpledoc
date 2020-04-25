package net.donotturnoff.simpledoc.server;

import java.util.Map;

class Request {

    private RequestMethod method;
    private String path;
    private String protocol;
    private Map<String, String> headers;
    private String body;

    Request(RequestMethod method, String path, String protocol, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
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
}
