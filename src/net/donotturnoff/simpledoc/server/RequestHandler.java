package net.donotturnoff.simpledoc.server;

import java.text.NumberFormat;
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
        if (requestString.isBlank()) {
            throw new RequestHandlingException("Request cannot be blank");
        }

        String[] lines = requestString.split("\\r\\n");

        // Extract first line parameters
        String firstLine = lines[0];
        if (!firstLine.matches("(GET|HEAD)(\\s+)(/\\S*)(\\s+)(SDTP/\\d+\\.\\d+)(\\s*)")) {
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
        String protocol = firstLineParts[2];

        // Extract headers
        int i = 1;
        Map<String, String> headers = new HashMap<>();
        for (; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                break;
            }
            String[] parts = lines[i].split("=", 1);
            if (parts.length != 2) {
                throw new RequestHandlingException("Invalid header syntax");
            }
            String key = parts[0].trim();
            String value = parts[1].trim();
            headers.put(key, value);
        }

        // Extract body
        StringBuilder sb = new StringBuilder();
        for (; i < lines.length; i++) {
            sb.append(lines[i]);
        }
        String body = sb.toString();

        return new Request(method, path, protocol, headers, body);
    }
}