package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.parsing.*;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Queue;

public class StyleWorker extends SwingWorker<Void, Void> {

    private final Page page;
    private final Element root;
    private final String body;

    public StyleWorker(Page page, Element root, String body) {
        this.page = page;
        this.root = root;
        this.body = body;
        page.addWorker(this);
    }

    @Override
    protected Void doInBackground() {
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
            page.error("Failed to lex stylesheet", e);
        }
        return tokens;
    }

    private void parseStylesheet(Queue<Terminal<?>> tokens) {
        try {
            SDSSParser parser = new SDSSParser(page);
            parser.parse(tokens);
        } catch (ParsingException e) {
            page.error("Failed to parse stylesheet", e);
        }
    }

    @Override
    public void done() {
        root.cascadeStyles();
        root.refresh(page);
        page.getPanel().repaint();
        page.getPanel().revalidate();
    }
}
