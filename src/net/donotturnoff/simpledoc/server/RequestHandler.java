package net.donotturnoff.simpledoc.server;

class RequestHandler {

    Request request;
    private String requestString;

    RequestHandler() {}
    RequestHandler(String requestString) {
        this.requestString = requestString;
    }

    String handle() {
        RequestHandler subHandler;
        try {
            this.request = parse(requestString);
            switch (request.getMethod()) {
                case GET:
                    subHandler = new GetHandler(request);
                    break;
                case HEAD:
                    subHandler = new HeadHandler(request);
                    break;
                default: throw new RequestHandlingException(Status.NOT_IMPLEMENTED, "Request method " + request.getMethod() + " not implemented");
            }
        } catch (RequestHandlingException e) {
            subHandler = new ErrorHandler(request, e);
        }
        return subHandler.handle();
    }

    private Request parse(String requestString) throws RequestHandlingException {
        return new Request();
    }
}
