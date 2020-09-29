package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.Status;

public class SDTPException extends Exception {
    public SDTPException(Status s) {
        super(s.toString());
    }
}
