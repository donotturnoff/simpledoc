package net.donotturnoff.simpledoc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
                default: throw new RequestHandlingException(501, "Unsupported request method: " + request.getMethod());
            }
        } catch (RequestHandlingException e) {
            subHandler = new ErrorHandler(request, e);
        }
        return subHandler.handle();
    }

    private Request parse(String requestString) {
        return new Request();
    }
}
