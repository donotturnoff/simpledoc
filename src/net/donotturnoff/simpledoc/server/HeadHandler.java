package net.donotturnoff.simpledoc.server;

import java.util.Map;

class HeadHandler {
    static Response handle(Request r) throws RequestHandlingException {
        Response getResponse = GetHandler.handle(r);
        String protocol = getResponse.getProtocol();
        Status status = getResponse.getStatus();
        Map<String, String> headers = getResponse.getHeaders();
        String body = "";
        return new Response(protocol, status, headers, body);
    }
}
