package net.donotturnoff.simpledoc.server;

class RequestHandlingException extends Exception {
    private int code;

    RequestHandlingException() {
        super();
        this.code = 0;
    }

    RequestHandlingException(int code) {
        super();
        this.code = code;
    }

    RequestHandlingException(String msg) {
        super(msg);
        this.code = 0;
    }

    RequestHandlingException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
