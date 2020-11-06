package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.parsing.*;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.FileUtils;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.lr0.*;
import net.donotturnoff.simpledoc.util.Status;

import javax.swing.*;
import java.awt.*;
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
        this.panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

    private void setFavicon(ImageIcon favicon, boolean gif) {
        this.favicon = SDTPBrowser.scaleFavicon(favicon, gif);
        updateTab();
    }

    public void offerFavicon(ImageIcon favicon, boolean gif) {
        this.pendingFavicon = favicon;
        this.pendingFaviconIsGif = gif;
        if (pendingResources.isEmpty()) {
            applyOfferedFavicon();
        }
    }

    private void applyOfferedFavicon() {
        setFavicon(this.pendingFavicon, this.pendingFaviconIsGif);
    }

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
        killWorkers();
        pendingResources.clear();
        offerFavicon(SDTPBrowser.ICON_FAVICON, true);
        setFavicon(SDTPBrowser.SPINNER_FAVICON, true);
        rv.clear();
        ev.updateTitle();
        rv.updateTitle();
        browser.setUrlBar(url);
        setTitle("Loading");
        addPendingResource(url);
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
        allElements.clear();
        panel.removeAll();
        if (!revisiting || !history.pageVisited(url)) {
            history.navigate(url);
            browser.setBackButtonState(history.canGoBack());
            browser.setForwardButtonState(history.canGoForward());
        }
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
        JImagePanel imgPanel = new JImagePanel(data, () -> info("Loaded " + url + ": " + status), (Exception e) -> error("Failed to load " + url, e));
        panel.add(imgPanel);
        panel.repaint();
        panel.revalidate();
        removePendingResource(url, response);
    }

    private void displayText(URL url, Response response) {
        byte[] data = response.getBody();
        Status status = response.getStatus();
        JTextArea ta = getTextPanel(data, browser.getConfig());
        panel.add(ta);
        panel.repaint();
        panel.revalidate();
        info("Loaded " + url + ": " + status);
        removePendingResource(url, response);
    }

    public void close() {
        killWorkers();
        ev.hide();
        rv.hide();
    }

    public void addWorker(SwingWorker<?, ?> worker) {
        workers.add(worker);
    }

    private void killWorkers() {
        for (SwingWorker<?, ?> worker: workers) {
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
        System.out.println(event);
        panel.removeAll();
    }

    public void errorHandler(Exception e) {
        error("Failed to load " + url, e);
    }

    public void addPendingResource(URL url) {
        pendingResources.add(url);
    }

    public void removePendingResource(URL url, Response response) {
        pendingResources.remove(url);
        if (pendingResources.isEmpty()) {
            applyOfferedFavicon();
        }
        rv.addResource(url, response);
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
}
