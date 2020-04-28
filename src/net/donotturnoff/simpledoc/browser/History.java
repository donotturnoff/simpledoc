package net.donotturnoff.simpledoc.browser;

import java.util.ArrayList;
import java.util.List;

class History {
    private List<String> docs;
    private int current;

    History() {
        docs = new ArrayList<>();
        current = 0;
    }

    boolean canGoBack() {
        return current > 0;
    }

    boolean canGoForward() {
        return current < docs.size()-1;
    }

    String jumpTo(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > docs.size()-1) {
            index = docs.size()-1;
        }
        current = index;
        return docs.get(current);
    }

    String back() {
        return jumpTo(current-1);
    }

    String forward() {
        return jumpTo(current+1);
    }

    void navigate(String site) {
        docs = docs.subList(0, current+1);
        docs.add(site);
    }
}
