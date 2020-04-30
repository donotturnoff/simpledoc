package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.util.Request;
import net.donotturnoff.simpledoc.util.RequestHandlingException;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.simpledoc.util.Status;

class RequestHandler {

    static Response handle(Request r) {
        Response response;
        try {
            switch (r.getMethod()) {
                case GET:
                    response = GetHandler.handle(r);
                case HEAD:
                    response = HeadHandler.handle(r);
                default: throw new RequestHandlingException(Status.NOT_IMPLEMENTED, "Request method " + r.getMethod() + " not implemented");
            }
        } catch (RequestHandlingException e) {
            response = ErrorHandler.handle(e);
        }
        addDefaultHeaders(response);
        return response;
    }

    private static void addDefaultHeaders(Response response) {
        String body = response.getBody();
        response.putHeader("length", Integer.toString(body.length()));
        response.putHeader("server", SDTPServer.config.getProperty("server"));
    }
}