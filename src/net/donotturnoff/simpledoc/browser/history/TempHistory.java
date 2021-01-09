package net.donotturnoff.simpledoc.browser.history;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TempHistory {
    private List<URL> docs;
    private int current;

    public TempHistory() {
        docs = new ArrayList<>();
        current = -1;
    }

    public List<URL> getDocs() {
        return List.copyOf(docs);
    }

    public boolean pageVisited(URL url) {
        return docs.contains(url);
    }

    public boolean canGoBack() {
        return current > 0;
    }

    public boolean canGoForward() {
        return current < docs.size()-1;
    }

    public URL jumpTo(int index) {
        if (index < 0) {
            index = 0;
        } else if (index > docs.size()-1) {
            index = docs.size()-1;
        }
        current = index;
        return docs.get(current);
    }

    public URL back() {
        return jumpTo(current-1);
    }

    public URL forward() {
        return jumpTo(current+1);
    }

    public void navigate(URL site) {
        current++;
        docs = docs.subList(0, current);
        docs.add(site);
    }
}
