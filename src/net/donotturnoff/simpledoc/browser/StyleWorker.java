package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.parsing.*;
import net.donotturnoff.simpledoc.browser.lexing.Token;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class StyleWorker extends SwingWorker<Void, Void> {

    private final Page page;
    private final Element root;
    private final String body;

    public StyleWorker(Page page, Element root, String body) {
        this.page = page;
        this.root = root;
        this.body = body;
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
            page.displayError(e);
        }
        return tokens;
    }

    private void parseStylesheet(Queue<Terminal<?>> tokens) {
        try {
            SDSSParser parser = new SDSSParser(page);
            parser.parse(tokens);
        } catch (ParsingException e) {
            page.displayError(e);
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
