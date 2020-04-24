package net.donotturnoff.simpledoc.server;

import java.util.Map;

class Request {

    private RequestMethod method;
    private String path;
    private String protocol;
    private Map<String, String> data;

    Request(RequestMethod method, String path, String protocol, Map<String, String> data) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.data = data;
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

    Map<String, String> getData() {
        return data;
    }
}
