package net.donotturnoff.simpledoc.browser.sdss;

import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.lexingbase.*;
import net.donotturnoff.lr0.*;
import net.donotturnoff.simpledoc.common.Response;

import javax.swing.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class StyleWorker extends SwingWorker<Void, Void> {

    private final Page page;
    private final Element root;
    private final String body;
    private final URL url;
    private final Response response;
    private final StyleSource source;
    private final int index;

    public StyleWorker(Page page, Element root, String body, URL url, Response response, StyleSource source, int index) {
        this.page = page;
        this.root = root;
        this.body = body;
        this.url = url;
        this.response = response;
        this.source = source;
        this.index = index;
        page.addWorker(this);
    }

    @Override
    protected Void doInBackground() {
        if (url == null) {
            page.info("Applying internal stylesheet");
        } else {
            page.info("Applying stylesheet " + url);
        }
        Queue<Terminal<?>> tokens = lexStylesheet(body);
        if (!tokens.isEmpty()) {
            parseStylesheet(tokens);
        }
        return null;
    }

    private Queue<Terminal<?>> lexStylesheet(String body) {
        Queue<Terminal<?>> tokens = new LinkedList<>();
        try {
            SDSSLexer lexer = new SDSSLexer(page);
            tokens = lexer.lex(body);
        } catch (LexingException e) {
            if (url == null) {
                page.warning("Failed to lex internal stylesheet: " + e.getMessage());
            } else {
                page.warning("Failed to lex stylesheet " + url + ": " + e.getMessage());
            }
        }
        return tokens;
    }

    private void parseStylesheet(Queue<Terminal<?>> tokens) {
        try {
            SDSSParser parser = new SDSSParser(page, source, index);
            parser.parse(tokens);
        } catch (ParsingException e) {
            if (url == null) {
                page.warning("Failed to parse internal stylesheet: " + e.getMessage());
            } else {
                page.warning("Failed to parse stylesheet " + url + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void done() {
        root.cascadeStyles(); // Pass updated inheritable styles down to children
        root.refresh();
        page.getPanel().repaint();
        page.getPanel().revalidate();
        page.removePendingResource(url, response);
        if (url == null) {
            page.info("Applied internal stylesheet");
        } else {
            page.info("Applied stylesheet " + url);
        }
    }
}
