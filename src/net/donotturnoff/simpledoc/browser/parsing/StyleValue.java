package net.donotturnoff.simpledoc.browser.parsing;

public class StyleValue implements Comparable<StyleValue> {
    private final String value;
    private final StyleSource source;
    private final int index;
    private int priority;

    public StyleValue(String value, StyleSource source, int index, int priority) {
        this.value = value;
        this.source = source;
        this.index = index;
        this.priority = priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(StyleValue sv) {
        if (sv == null) {
            return 1;
        } else if (source.compareTo(sv.source) < 0) {
            return -1;
        } else if (source.compareTo(sv.source) > 0) {
            return 1;
        } else if (index < sv.index) {
            return -1;
        } else if (index > sv.index) {
            return 1;
        } else if (priority < sv.priority) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "StyleValue{" +
                "value=\"" + value + '"' +
                ", source=" + source +
                ", index=" + index +
                ", priority=" + priority +
                '}';
    }
}
