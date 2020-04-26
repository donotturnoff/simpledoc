package net.donotturnoff.simpledoc.server;

class RequestHandler {
    static Response handle(Request r) {
        try {
            switch (r.getMethod()) {
                case GET:
                    return GetHandler.handle(r);
                case HEAD:
                    return HeadHandler.handle(r);
                default: throw new RequestHandlingException(Status.NOT_IMPLEMENTED, "Request method " + r.getMethod() + " not implemented");
            }
        } catch (RequestHandlingException e) {
            return ErrorHandler.handle(e);
        }
    }
}