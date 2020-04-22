package net.donotturnoff.simpledoc.server;

class BadRequestException extends RequestHandlingException {

    BadRequestException() {
        super(400);
    }

    BadRequestException(String msg) {
        super(msg, 400);
    }
}
