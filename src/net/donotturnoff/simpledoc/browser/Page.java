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

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(40);
    }

    public void setTabTitle(String title) {
        JTabbedPane tabbedPane = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, panel);
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (SwingUtilities.isDescendingFrom(panel, tabbedPane.getComponentAt(i))) {
                tabbedPane.setTitleAt(i, title);
                break;
            }
        }
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
        load(history.back());
    }

    public void forward() {
        revisiting = true;
        load(history.forward());
    }

    public void reload() {
        revisiting = true;
        load(url);
    }

    public void navigate(String s) {
        try {
            url = ConnectionUtils.getURL(url, s);
        } catch (MalformedURLException e) {
            displayError(e);
        }
        revisiting = false;
        load(url);
    }

    private void load(URL url) {
        this.url = url;
        browser.setUrlBar(url);
        setTabTitle("Loading");
        ConnectionWorker worker = new ConnectionWorker(this);
        worker.execute();
    }

    public Void loaded(Response response) {
        this.data = response;
        if (!revisiting || !history.pageVisited(url)) {
            history.navigate(url);
        }
        List<Token<?>> tokens = lex(new String(data.getBody()));
        if (!tokens.isEmpty()) {
            Element root = parse(tokens);
            if (root != null) {
                style(root);
                render(root);
            }
        }
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

    public void displayWarning(String w) {
        JOptionPane.showMessageDialog(panel, w, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public void displayError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel, e, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
