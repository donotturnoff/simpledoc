package net.donotturnoff.simpledoc.server;

class ErrorHandler extends RequestHandler {

    private final RequestHandlingException exception;

    ErrorHandler(Request request, RequestHandlingException e) {
        this.request = request;
        this.exception = e;
    }
}
