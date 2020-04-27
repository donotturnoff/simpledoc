package net.donotturnoff.simpledoc.server;

import java.util.HashMap;
import java.util.Map;

class ErrorHandler {
    static Response handle(RequestHandlingException e) {
        String protocol = SDTPServer.DEFAULT_PROTOCOL;
        Map<String, String> headers = new HashMap<>();
        headers.put("type", "text/sdml");
        String sb = "doctype(SDML/1.0)\n\n" +
                "doc(charset=\"UTF-8\"\n" +
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
        return new Response(protocol, e.getStatus(), headers, sb);
    }
}
