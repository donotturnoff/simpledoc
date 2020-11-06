package net.donotturnoff.simpledoc.browser;

import java.time.LocalDateTime;

public class BrowserEvent {
    public final static int ERROR = 0, WARNING = 1, INFO = 2;
    private final static String[] typeNames = new String[]{"ERROR", "WARNING", "INFO"};

    private final LocalDateTime timestamp;
    private final int type;
    private final String message;

    public BrowserEvent(int type, String message) {
        timestamp = LocalDateTime.now();
        this.type = type;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "(" + timestamp + ") [" + String.format("%1$-" + 8 + "s", typeNames[type]) + "] " + message;
    }

    public Object[] toTableRow() {
        return new Object[]{timestamp, typeNames[type], message};
    }
}
