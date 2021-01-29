package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.common.RequestHandlingException;
import net.donotturnoff.simpledoc.common.Response;

import java.util.HashMap;
import java.util.Map;

class ErrorHandler {
    static Response handle(RequestHandlingException e) {
        String protocol = SDTPServer.DEFAULT_PROTOCOL;
        Map<String, String> headers = new HashMap<>();
        headers.put("type", "text/sdml");
        String body = "doc(version=\"SDML/1.0\", charset=\"UTF-8\") {\n" +
                " head {\n" +
                "  title {\"" +
                e.getStatus().toString() +
                "\"}\n" +
                " }\n" +
                " body {\n" +
                "  h1 {\"" +
                e.getStatus().toString() +
                "\"}\n" +
                "  p {\"" +
                e.getMessage() +
                "\"}\n" +
                " }\n" +
                "}\n";
        headers.put("length", Integer.toString(body.length()));
        return new Response(protocol, e.getStatus(), headers, body.getBytes());
    }
}
