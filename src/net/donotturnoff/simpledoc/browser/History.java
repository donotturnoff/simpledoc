package net.donotturnoff.simpledoc.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class History {
    private List<URL> docs;
    private int current;

    History() {
        docs = new ArrayList<>();
        current = -1;
    }

    List<URL> getDocs() {
        return List.copyOf(docs);
    }

    boolean pageVisited(URL url) {
        return docs.contains(url);
    }

    boolean canGoBack() {
        return current > 0;
    }

    boolean canGoForward() {
        return current < docs.size()-1;
    }

    URL jumpTo(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > docs.size()-1) {
            index = docs.size()-1;
        }
        current = index;
        return docs.get(current);
    }

    URL back() {
        return jumpTo(current-1);
    }

    URL forward() {
        return jumpTo(current+1);
    }

    void navigate(URL site) {
        current++;
        docs = docs.subList(0, current);
        docs.add(site);
    }
}
