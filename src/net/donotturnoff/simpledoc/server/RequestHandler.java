package net.donotturnoff.simpledoc.server;

import java.util.HashMap;
import java.util.Map;

class RequestHandler {

    Request request;
    private String requestString;

    RequestHandler() {
        this.requestString = "";
    }
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
        if (requestString.length() == 0) {
            throw new RequestHandlingException("Request cannot be blank");
        }
        String[] lines = requestString.split("\\r|\\r?\\n");
        String firstLine = lines[0];
        if (!firstLine.matches("(GET|HEAD)(\\s+)(/\\S*)(\\s+)(SDTP/\\d+\\.\\d+)(\\s*)(\\{?)")) {
            throw new RequestHandlingException("First line of request must be of format [method] [path] [protocol]");
        }
        String[] firstLineParts = firstLine.split("\\s+");
        RequestMethod method;
        try {
            method = RequestMethod.valueOf(firstLineParts[0]);
        } catch (IllegalArgumentException e) {
            throw new RequestHandlingException("Invalid request method used");
        }
        String path = firstLineParts[1];
        String protocol = firstLineParts[2].replace("{", "");
        Map<String, String> data = new HashMap<>();
        return new Request(method, path, protocol, data);
    }
}
