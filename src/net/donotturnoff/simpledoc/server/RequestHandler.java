package net.donotturnoff.simpledoc.server;

import net.donotturnoff.simpledoc.common.Request;
import net.donotturnoff.simpledoc.common.RequestHandlingException;
import net.donotturnoff.simpledoc.common.Response;
import net.donotturnoff.simpledoc.common.Status;

class RequestHandler {

    static Response handle(Request r) {
        Response response;
        try {
            switch (r.getMethod()) {
                case GET:
                    response = GetHandler.handle(r); break;
                case HEAD:
                    response = HeadHandler.handle(r); break;
                default: throw new RequestHandlingException(Status.NOT_IMPLEMENTED, "Request method " + r.getMethod() + " not implemented"); // Error 501
            }
        } catch (RequestHandlingException e) {
            response = ErrorHandler.handle(e);
        }
        addDefaultHeaders(response);
        return response;
    }

    // Add headers which apply to all responses
    private static void addDefaultHeaders(Response response) {
        byte[] body = response.getBody();
        response.putHeader("length", Integer.toString(body.length));
        response.putHeader("server", SDTPServer.config.getProperty("server"));
    }
}