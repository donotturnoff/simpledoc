package net.donotturnoff.simpledoc.server;

import java.util.Map;

class ErrorHandler {
    static Response handle(RequestHandlingException e) {
        String protocol = "SDTP/0.1";
        Status status = Status.INTERNAL_SERVER_ERROR;
        Map<String, String> headers = Map.of();
        String body = "Error";
        return new Response(protocol, status, headers, body);
    }
}
