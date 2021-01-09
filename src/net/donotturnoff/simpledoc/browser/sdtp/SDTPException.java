package net.donotturnoff.simpledoc.browser.sdtp;

import net.donotturnoff.simpledoc.common.Status;

public class SDTPException extends Exception {
    public SDTPException(Status s) {
        super(s.toString());
    }
}
