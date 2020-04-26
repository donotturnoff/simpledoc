package net.donotturnoff.simpledoc.server;

import java.util.Map;

class GetHandler {
    static Response handle(Request r) {
        String protocol = SDTPServer.DEFAULT_PROTOCOL;
        Status status = Status.OK;
        Map<String, String> headers = Map.of();
        String body = "GET";
        return new Response(protocol, status, headers, body);
    }
}
