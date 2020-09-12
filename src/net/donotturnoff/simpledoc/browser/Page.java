package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.parsing.*;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Page {

    private final SDTPBrowser browser;
    private final JPanel panel;
    private final JScrollPane scrollPane;
    private URL url;
    private Response data;
    private final History history;
    private boolean revisiting;
    private Element root;
    private final Set<Element> allElements;
    private final List<BrowserEvent> events;

    Page(SDTPBrowser browser) {
        this.browser = browser;
        this.panel = new JPanel();
        this.scrollPane = new JScrollPane(panel);
        this.history = new History();
        this.revisiting = false;
        this.url = null;
        this.allElements = new HashSet<>();
        this.events = new ArrayList<>();

        browser.setBackButtonState(false);
        browser.setForwardButtonState(false);

        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(40);
    }

    public void setTabTitle(String title) {
        JTabbedPane tabbedPane = browser.getTabbedPane();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(panel, tabbedPane.getComponentAt(i))) {
                browser.setTitle(i, title);
                Component tabComponent = tabbedPane.getTabComponentAt(i);
                if (tabComponent instanceof CustomTabComponent) {
                    ((CustomTabComponent) tabComponent).updateSize();
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
        browser.setUrlBar(url);
        setTabTitle("Loading");
        info("Navigated to " + url);
        ConnectionWorker worker = new ConnectionWorker(url,this);
        worker.execute();
    }

    public Void loaded(URL url, Response response) {
        data = response;
        allElements.clear();
        if (!revisiting || !history.pageVisited(url)) {
            history.navigate(url);
            browser.setBackButtonState(history.canGoBack());
            browser.setForwardButtonState(history.canGoForward());
        }
        String type = response.getHeaders().get("type");
        String generalType = type.split("/")[0];
        if (generalType.equals("image")) {
            displayImage(data.getBody());
        } else if (type.equals("text/sdml")) {
            try {
                setTabTitle(Paths.get(new URI(url.toString()).getPath()).getFileName().toString());
            } catch (URISyntaxException e) {
                setTabTitle(url.getFile());
            }
            Queue<Terminal<?>> tokens = lex(new String(data.getBody()));
            if (!tokens.isEmpty()) {
                Element root = parse(tokens);
                if (root != null) {
                    render();
                }
            }
        }
        info("Loaded " + url);
        return null;
    }

    private Queue<Terminal<?>> lex(String body) {
        Queue<Terminal<?>> tokens = new LinkedList<>();
        try {
            SDMLLexer lexer = new SDMLLexer(this);
            tokens = lexer.lex(body);
        } catch (LexingException e) {
            error("Failed to lex page: " + e.getMessage());
        }
        return tokens;
    }

    private Element parse(Queue<Terminal<?>> tokens) {
        root = null;
        try {
            SDMLParser parser = new SDMLParser(this);
            root = parser.parse(tokens);
        } catch (ParsingException e) {
            error("Failed to parse page: " + e.getMessage());
        }
        return root;
    }

    public void render() {
        panel.removeAll();
        if (data != null) {
            RenderWorker worker = new RenderWorker(this, root);
            worker.execute();
        }
    }

    private void displayImage(byte[] data) {
        panel.removeAll();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage img;
        try {
            img = ImageIO.read(bais);
            if (img == null) {
                throw new IOException("No data or unrecognised image format");
            }
            JImagePanel imgPanel = new JImagePanel(img);
            imgPanel.setBackground(Color.WHITE);
            panel.add(imgPanel);
            panel.repaint();
            panel.revalidate();
            setTabTitle(Paths.get(new URI(url.toString()).getPath()).getFileName().toString());
        } catch (IOException e) {
            error("Failed to load image: " + e.getMessage());
        } catch (URISyntaxException e) {
            setTabTitle(url.getFile());
        }
    }

    public void info(String i) {
        events.add(new BrowserEvent(BrowserEvent.INFO, i));
        setStatus(i);
    }

    public void warning(String w) {
        events.add(new BrowserEvent(BrowserEvent.WARNING, w));
        setStatus(w);
    }

    public void error(String e) {
        BrowserEvent event = new BrowserEvent(BrowserEvent.ERROR, e);
        events.add(event);
        setStatus(e);
        setTabTitle("Error");
        System.out.println(event);
        panel.removeAll();
    }

    public void addElement(Element e) {
        allElements.add(e);
    }

    public Set<Element> getAllElements() {
        return allElements;
    }
}
