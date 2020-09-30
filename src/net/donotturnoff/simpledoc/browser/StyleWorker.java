package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.parsing.*;
import net.donotturnoff.lr0.*;
import net.donotturnoff.simpledoc.util.Response;

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

    public StyleWorker(Page page, Element root, String body, URL url, Response response) {
        this.page = page;
        this.root = root;
        this.body = body;
        this.url = url;
        this.response = response;
        page.addWorker(this);
    }

    @Override
    protected Void doInBackground() {
        page.info("Applying stylesheet " + url);
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
            page.warning("Failed to lex stylesheet: " + e.getMessage());
        }
        return tokens;
    }

    private void parseStylesheet(Queue<Terminal<?>> tokens) {
        try {
            SDSSParser parser = new SDSSParser(page);
            parser.parse(tokens);
        } catch (ParsingException e) {
            page.warning("Failed to parse stylesheet: " + e.getMessage());
        }
    }

    @Override
    public void done() {
        root.cascadeStyles();
        root.refresh(page);
        page.getPanel().repaint();
        page.getPanel().revalidate();
        page.removePendingResource(url, response);
        page.info("Applied stylesheet " + url);
    }
}
