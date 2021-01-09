package net.donotturnoff.simpledoc.browser.history;

import net.donotturnoff.simpledoc.browser.SDTPBrowser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

// TODO: switch to JSON or other more suitable format
public class HistoryStorageHandler {

    private final SDTPBrowser browser;

    public HistoryStorageHandler(SDTPBrowser browser) {
        this.browser = browser;
    }

    public void add(URL url) throws IOException {
        // Add url to history if configured to store history
        // File is stored with oldest entry first and new entries appended, then read in reverse
        // This is faster than adding to the start of the file
        if (browser.getConfig().getProperty("store_history").equals("true")) {
            Date datetime = new Date();
            String timestamp = String.valueOf(datetime.getTime());
            String historyEntry = timestamp + " " + url + "\n";

            File historyFile = new File(browser.getConfig().getProperty("history_file"));
            //noinspection ResultOfMethodCallIgnored
            historyFile.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(historyFile, true));
            bw.write(historyEntry);
            bw.close();
        }
    }

    public void clear() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(browser.getConfig().getProperty("history_file"));
        pw.close();
    }

    public SortedMap<Date, URL> get(int start, int len) throws IOException {
        SortedMap<Date, URL> visits = new TreeMap<>(Collections.reverseOrder());

        File historyFile = new File(browser.getConfig().getProperty("history_file"));
        BufferedReader br = new BufferedReader(new FileReader(historyFile));
        int entries = getHistoryLength();
        String line;
        int i = 0;

        // Start at start+len entries from the end of the history file since we are reading in reverse order
        // Add each valid line to the map to return (which also stores in reversed order)
        while ((line = br.readLine()) != null) {
            if (i >= entries - (start + len)) {
                if (i >= entries - start) {
                    break;
                }
                if (!line.isBlank()) {
                    try {
                        String[] parts = line.split(" ");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException();
                        }
                        Date datetime = new Date(Long.parseLong(parts[0]));
                        URL url = new URL(parts[1]);
                        visits.put(datetime, url);
                    } catch (MalformedURLException | IllegalArgumentException ignored) {

                    }
                }
            }
            i++; // TODO: only count valid lines
        }
        return visits;
    }

    public int getHistoryLength() {
        int len = 0;

        try {
            File historyFile = new File(browser.getConfig().getProperty("history_file"));
            BufferedReader br = new BufferedReader(new FileReader(historyFile));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    try {
                        String[] parts = line.split(" ");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException();
                        }
                        Date datetime = new Date(Long.parseLong(parts[0]));
                        URL url = new URL(parts[1]);
                        len++; // Incrementing counter here prevents counting invalid lines
                    } catch (MalformedURLException | IllegalArgumentException ignored) {

                    }
                }
            }
        } catch (IOException ignored) {

        }

        return len;
    }

}
