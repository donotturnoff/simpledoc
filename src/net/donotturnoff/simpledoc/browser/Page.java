package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.components.JImagePanel;
import net.donotturnoff.simpledoc.browser.components.PageTabComponent;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.events.BrowserEvent;
import net.donotturnoff.simpledoc.browser.events.EventViewer;
import net.donotturnoff.simpledoc.browser.history.TempHistory;
import net.donotturnoff.simpledoc.browser.parsing.*;
import net.donotturnoff.simpledoc.browser.resources.ResourceViewer;
import net.donotturnoff.simpledoc.browser.sdml.RenderWorker;
import net.donotturnoff.simpledoc.browser.sdml.SDMLLexer;
import net.donotturnoff.simpledoc.browser.sdml.SDMLParser;
import net.donotturnoff.simpledoc.browser.sdtp.ConnectionWorker;
import net.donotturnoff.simpledoc.browser.sdtp.SDTPException;
import net.donotturnoff.simpledoc.common.ConnectionUtils;
import net.donotturnoff.simpledoc.common.FileUtils;
import net.donotturnoff.simpledoc.common.Response;
import net.donotturnoff.lr0.*;
import net.donotturnoff.simpledoc.common.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Page {

    public static JTextArea getTextPanel(byte[] data, Properties config) {
        int fontSize;
        try {
            fontSize = Integer.parseInt(config.getProperty("plain_text_font_size"));
            if (fontSize < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            fontSize = 11;
        }
        Font font = new Font(config.getProperty("plain_text_font_family"), Font.PLAIN, fontSize);
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setText(new String(data));
        ta.setFont(font);
        ta.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        ta.setCaretPosition(0);
        return ta;
    }

    private final SDTPBrowser browser;
    private final JPanel panel;
    private final JScrollPane scrollPane;
    private URL url;
    private String filename;
    private Response data;
    private final TempHistory history;
    private boolean revisiting;
    private Element root;
    private final Set<Element> allElements;
    private final Set<URL> pendingResources;
    private final EventViewer ev;
    private final ResourceViewer rv;
    private final Set<SwingWorker<?, ?>> workers;
    private String title;
    private ImageIcon favicon, pendingFavicon;
    private boolean pendingFaviconIsGif;

    Page(SDTPBrowser browser) {
        this.browser = browser;
        this.panel = new JPanel();
        this.scrollPane = new JScrollPane(panel);
        this.history = new TempHistory();
        this.revisiting = false;
        this.url = null;
        this.filename = null;
        this.allElements = new HashSet<>();
        this.pendingResources = new HashSet<>();
        this.ev = new EventViewer(this);
        this.rv = new ResourceViewer(this);
        this.workers = new HashSet<>();
        setTitle("New tab");
        setFavicon(SDTPBrowser.ICON_FAVICON, true);
        offerFavicon(SDTPBrowser.ICON_FAVICON, true);

        browser.setBackButtonState(false);
        browser.setForwardButtonState(false);

        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(40);

        panel.setBackground(Color.WHITE);
    }

    public void setTitle(String title) {
        this.title = title;
        updateTab();
    }

    // Set favicon with immediate effect
    private void setFavicon(ImageIcon favicon, boolean gif) {
        this.favicon = SDTPBrowser.scaleFavicon(favicon, gif);
        updateTab();
    }

    // Set favicon to use once loading has finished
    public void offerFavicon(ImageIcon favicon, boolean gif) {
        this.pendingFavicon = favicon;
        this.pendingFaviconIsGif = gif;
        if (pendingResources.isEmpty()) {
            applyOfferedFavicon();
        }
    }

    // Once loading has finished, set the offered favicon
    private void applyOfferedFavicon() {
        setFavicon(this.pendingFavicon, this.pendingFaviconIsGif);
    }

    // Refreshes tab widget on title or favicon update
    private void updateTab() {
        JTabbedPane tabbedPane = browser.getTabbedPane();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(panel, tabbedPane.getComponentAt(i))) {
                browser.setTitle(i, title);
                Component tabComponent = tabbedPane.getTabComponentAt(i);
                if (tabComponent instanceof PageTabComponent) {
                    ((PageTabComponent) tabComponent).updateLabel();
                }
                break;
            }
        }
    }

    public void setStatus(String status) {
        browser.setStatus(status);
    }

    public SDTPBrowser getBrowser() {
        return browser;
    }

    public String getFilename() {
        return filename;
    }

    public URL getUrl() {
        return url;
    }

    public JPanel getPanel() {
        return panel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public Response getData() {
        return data;
    }

    public Element getRoot() {
        return root;
    }

    public String getTitle() {
        return title;
    }

    public Icon getFavicon() {
        return favicon;
    }

    public void back() {
        revisiting = true;
        URL back = history.back();
        browser.setBackButtonState(history.canGoBack());
        browser.setForwardButtonState(history.canGoForward());
        load(back);
    }

    public void forward() {
        revisiting = true;
        URL forward = history.forward();
        browser.setBackButtonState(history.canGoBack());
        browser.setForwardButtonState(history.canGoForward());
        load(forward);
    }

    public void reload() {
        revisiting = true;
        load(url);
    }

    public void navigate(String s, boolean externalInput) {
        URL url;
        try {
            if (!externalInput) {
                url = ConnectionUtils.getURL(this.url, s);
            } else {
                url = new URL(s);
            }
            revisiting = false;
            load(url);
        } catch (MalformedURLException e) {
            error("Failed to load " + s + ": " + e.getMessage());
        }
    }

    private void load(URL url) {
        this.url = url;
        filename = FileUtils.getFilename(url);

        // Cancel loading of previous page
        killWorkers();
        pendingResources.clear();

        // Offer default favicon for fully-loaded page (may be overridden in SDML), and set loading favicon
        offerFavicon(SDTPBrowser.ICON_FAVICON, true);
        setFavicon(SDTPBrowser.SPINNER_FAVICON, true);

        // Reset resource and event viewers
        rv.clear();
        ev.updateTitle();
        rv.updateTitle();

        // Update interface
        browser.setUrlBar(url);
        setTitle("Loading");

        addPendingResource(url);

        // Start load
        ConnectionWorker worker = new ConnectionWorker(url,this);
        worker.execute();
    }

    public void loaded(URL url, Response response) {
        Status s = response.getStatus();
        if (s == Status.OK) {
            info("Loaded " + url + ": " + s);
        } else {
            error("Failed to load " + url, new SDTPException(s));
        }
        setTitle(filename);
        data = response;

        // Reset page on load
        allElements.clear();
        panel.removeAll();

        // Only add to history if we are visiting the page anew (not going back/forward or refreshing)
        if (!revisiting || !history.pageVisited(url)) {
            history.navigate(url);
            browser.addToHistory(this, url);
            browser.setBackButtonState(history.canGoBack());
            browser.setForwardButtonState(history.canGoForward());
        }

        // Render page according to MIME type
        String type = response.getHeaders().get("type");
        String generalType = type.split("/")[0];
        if (type.equals("text/sdml")) {
            Queue<Terminal<?>> tokens = lex(new String(data.getBody()));
            if (!tokens.isEmpty()) {
                Element root = parse(tokens);
                if (root != null) {
                    render();
                }
            }
        } else if (generalType.equals("image")) {
            displayImage(url, data);
        } else if (generalType.equals("text")) {
            displayText(url, data);
        }
    }

    private Queue<Terminal<?>> lex(String body) {
        Queue<Terminal<?>> tokens = new LinkedList<>();
        try {
            SDMLLexer lexer = new SDMLLexer(this);
            tokens = lexer.lex(body);
        } catch (LexingException e) {
            error("Failed to lex page", e);
        }
        return tokens;
    }

    private Element parse(Queue<Terminal<?>> tokens) {
        root = null;
        try {
            SDMLParser parser = new SDMLParser(this);
            root = parser.parse(tokens);
        } catch (ParsingException e) {
            error("Failed to parse page", e);
        }
        return root;
    }

    public void render() {
        if (data != null) {
            RenderWorker worker = new RenderWorker(this, root);
            worker.execute();
        }
    }

    private void displayImage(URL url, Response response) {
        byte[] data = response.getBody();
        Status status = response.getStatus();
        JImagePanel imgPanel = new JImagePanel(data, () -> info("Loaded " + url + ": " + status), (Exception e) -> error("Failed to load " + url, e), true);

        // Zoom on click
        imgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                imgPanel.toggleScaled();
            }
        });

        panel.add(imgPanel);
        panel.repaint();
        panel.revalidate();
        removePendingResource(url, response, 0);
    }

    private void displayText(URL url, Response response) {
        byte[] data = response.getBody();
        Status status = response.getStatus();
        JTextArea ta = getTextPanel(data, browser.getConfig());
        panel.add(ta);
        panel.repaint();
        panel.revalidate();
        info("Loaded " + url + ": " + status);
        removePendingResource(url, response, 0);
    }

    public void close() {
        killWorkers();
        ev.hide();
        rv.hide();
    }

    public synchronized void addWorker(SwingWorker<?, ?> worker) {
        workers.add(worker);
    }

    // Cancel all currently-executing threads before navigation
    private synchronized void killWorkers() {
        for (SwingWorker<?, ?> worker : workers) {
            worker.cancel(true);
        }
        workers.clear();
    }

    public void info(String i) {
        ev.addEvent(new BrowserEvent(BrowserEvent.INFO, i));
        setStatus(i);
    }

    public void warning(String w) {
        ev.addEvent(new BrowserEvent(BrowserEvent.WARNING, w));
        setStatus(w);
    }

    public void error(String prefix, Exception e) {
        error(prefix + ": " + e);
        e.printStackTrace();
    }

    public void error(String e) {
        BrowserEvent event = new BrowserEvent(BrowserEvent.ERROR, e);
        ev.addEvent(event);
        setStatus(e);
        setTitle("Error");
        setFavicon(SDTPBrowser.ERROR_FAVICON, true);
        System.err.println(event);
        panel.removeAll();
    }

    public void errorHandler(Exception e) {
        error("Failed to load " + url, e);
    }

    public void addPendingResource(URL url) {
        pendingResources.add(url);
    }

    public void removePendingResource(URL url, Response response) {
        removePendingResource(url, response, -1);
    }

    public void removePendingResource(URL url, Response response, int index) {
        pendingResources.remove(url);

        // Set offered favicon if all resources have loaded
        if (pendingResources.isEmpty()) {
            applyOfferedFavicon();
        }

        // Excludes internal resources (e.g. internal stylesheets)
        if (response != null) {
            rv.addResource(url, response, index);
        }
    }

    public void addElement(Element e) {
        allElements.add(e);
    }

    public Set<Element> getAllElements() {
        return allElements;
    }

    public void toggleEventViewer() {
        ev.toggle();
    }

    public void toggleResourceViewer() {
        rv.toggle();
    }

    public void showEventViewer() {
        ev.show();
    }

    public void showResourceViewer() {
        rv.show();
    }
}
