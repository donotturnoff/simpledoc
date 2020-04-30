package net.donotturnoff.simpledoc.util;

public class RequestHandlingException extends Exception {
    private Status status = Status.INTERNAL_SERVER_ERROR;

    RequestHandlingException() {
        super();
    }

    RequestHandlingException(Status status) {
        super();
        this.status = status;
    }

    RequestHandlingException(String msg) {
        super(msg);
    }

    public RequestHandlingException(Status status, String msg) {
        super(msg);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
