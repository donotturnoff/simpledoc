package net.donotturnoff.simpledoc.server;

class ErrorHandler {
    static String handle(RequestHandlingException e) {
        return "Error";
    }
}
