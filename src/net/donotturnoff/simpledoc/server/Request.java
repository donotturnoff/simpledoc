package net.donotturnoff.simpledoc.server;

import java.util.HashMap;

class Request {

    private RequestMethod method;
    private String path;
    private String protocol;
    private HashMap<String, String> data;

    RequestMethod getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getProtocol() {
        return protocol;
    }

    HashMap<String, String> getData() {
        return data;
    }
}
