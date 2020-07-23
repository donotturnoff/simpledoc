package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.util.Request;
import net.donotturnoff.simpledoc.util.RequestHandlingException;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.simpledoc.util.Status;

import java.util.Map;

class HeadHandler {
    static Response handle(Request r) throws RequestHandlingException {
        Response getResponse = GetHandler.handle(r);
        String protocol = getResponse.getProtocol();
        Status status = getResponse.getStatus();
        Map<String, String> headers = getResponse.getHeaders();
        String body = "";
        return new Response(protocol, status, headers, new byte[0]);
    }
}
