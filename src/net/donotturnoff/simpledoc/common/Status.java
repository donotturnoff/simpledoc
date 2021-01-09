package net.donotturnoff.simpledoc.common;

import java.util.HashMap;
import java.util.Map;

public enum Status {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented");

    private final static Map<Integer, Status> codeMap = new HashMap<>();

    static {
        for (Status status: values()) {
            codeMap.put(status.getCode(), status);
        }
    }

    private final int code;
    private final String msg;

    Status(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Status getByCode(int code) {
        return codeMap.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return code + " " + msg;
    }
}
