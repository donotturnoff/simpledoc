package net.donotturnoff.simpledoc.server;

import java.util.Map;

class HeadHandler {
    static Response handle(Request r) {
        String protocol = r.getProtocol();
        Status status = Status.OK;
        Map<String, String> headers = Map.of();
        String body = "HEAD";
        return new Response(protocol, status, headers, body);
    }
}
