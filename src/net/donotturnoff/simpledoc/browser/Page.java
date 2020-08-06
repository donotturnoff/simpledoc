package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.lexing.LexingException;
import net.donotturnoff.simpledoc.browser.lexing.SDMLLexer;
import net.donotturnoff.simpledoc.browser.lexing.Token;
import net.donotturnoff.simpledoc.browser.lexing.TokenType;
import net.donotturnoff.simpledoc.browser.parsing.ParsingException;
import net.donotturnoff.simpledoc.browser.parsing.SDMLParser;
import net.donotturnoff.simpledoc.browser.styling.SDMLStyler;
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
import java.util.ArrayList;
import java.util.List;

public class Page {

    private final SDTPBrowser browser;
    private final JPanel panel;
    private final JScrollPane scrollPane;
    private URL url;
    private Response data;
    private final History history;
    private boolean revisiting;

    Page(SDTPBrowser browser) {
        this.browser = browser;
        this.panel = new JPanel();
        this.scrollPane = new JScrollPane(panel);
        this.history = new History();
        this.revisiting = false;
        this.url = null;

        browser.setBackButtonState(false);
        browser.setForwardButtonState(false);

        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(40);
    }

    public void setTabTitle(String title) {
        JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, panel);
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(panel, tabbedPane.getComponentAt(i))) {
                browser.setTitle(i, title);
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

    public void navigate(String s) {
        URL url;
        try {
            url = ConnectionUtils.getURL(this.url, s);
            revisiting = false;
            load(url);
        } catch (MalformedURLException e) {
            displayError(e);
        }
    }

    private void load(URL url) {
        browser.setUrlBar(url);
        setTabTitle("Loading");
        setStatus("Loading " + url);
        ConnectionWorker worker = new ConnectionWorker(url,this);
        worker.execute();
    }

    public Void loaded(URL url, Response response) {
        this.url = url;
        this.data = response;
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
            List<Token<?>> tokens = lex(new String(data.getBody()));
            if (!tokens.isEmpty()) {
                Element root = parse(tokens);
                if (root != null) {
                    style(root);
                    render(root);
                }
            }
        }
        setStatus("Loaded " + url);
        return null;
    }

    private List<Token<?>> lex(String body) {
        List<Token<?>> tokens = new ArrayList<>();
        try {
            SDMLLexer lexer = new SDMLLexer(this, body);
            Token<?> t;
            do {
                t = lexer.nextToken();
                tokens.add(t);
            } while (t.getType() != TokenType.EOF);
        } catch (LexingException e) {
            displayError(e);
        }
        return tokens;
    }

    private Element parse(List<Token<?>> tokens) {
        Element root = null;
        try {
            SDMLParser parser = new SDMLParser(this, tokens);
            root = parser.parse();
        } catch (ParsingException e) {
            displayError(e);
        }
        return root;
    }

    private void style(Element root) {
        SDMLStyler styler = new SDMLStyler();
        styler.style(root);
    }

    private void render(Element root) {
        panel.removeAll();
        if (data != null) {
            root.render(this, panel);
            panel.repaint();
            panel.revalidate();
        }
    }

    private void displayImage(byte[] data) {
        panel.removeAll();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage img;
        try {
            img = ImageIO.read(bais);
            if (img == null) {
                throw new IOException("No data or unrecognised format");
            }
            JImagePanel imgPanel = new JImagePanel(img);
            imgPanel.setBackground(Color.WHITE);
            panel.add(imgPanel);
            panel.repaint();
            panel.revalidate();
            setTabTitle(Paths.get(new URI(url.toString()).getPath()).getFileName().toString());
        } catch (IOException e) {
            setStatus("Failed to load " + url);
        } catch (URISyntaxException e) {
            setTabTitle(url.getFile());
        }
    }

    public void displayWarning(String w) {
        JOptionPane.showMessageDialog(panel, w, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public void displayError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel, e, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
