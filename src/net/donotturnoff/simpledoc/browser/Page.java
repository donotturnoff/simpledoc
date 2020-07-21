package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.browser.element.DocElement;
import net.donotturnoff.simpledoc.browser.element.Element;
import net.donotturnoff.simpledoc.browser.lexing.LexingException;
import net.donotturnoff.simpledoc.browser.lexing.SDMLLexer;
import net.donotturnoff.simpledoc.browser.lexing.Token;
import net.donotturnoff.simpledoc.browser.lexing.TokenType;
import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Page {

    private final JPanel panel;
    private URL url;
    private Response data;
    private final History history;
    private boolean revisiting;

    Page() {
        this.panel = new JPanel();
        this.history = new History();
        this.revisiting = false;
    }

    public URL getUrl() {
        return url;
    }

    public JPanel getPanel() {
        return panel;
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
            URL url = new URL(s);
            if (!url.getProtocol().equals("sdtp")) {
                throw new MalformedURLException("Scheme must be sdtp");
            }
            this.url = url;
            revisiting = false;
            load(url);
        } catch (MalformedURLException e) {
            displayError(e);
        }
    }

    private void load(URL url) {
        this.url = url;
        ConnectionWorker worker = new ConnectionWorker(this);
        worker.execute();
    }

    public void loaded(Response response) {
        this.data = response;
        if (!revisiting) {
            history.navigate(url);
        }
        List<Token<?>> tokens = lex(data.getBody());
        if (!tokens.isEmpty()) {
            Element root = parse(tokens);
            render(root);
        }
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
        return new DocElement(Map.of(), List.of());
    }

    private void render(Element root) {
        panel.removeAll();
        if (data != null) {
            root.render(panel);
            panel.repaint();
            panel.revalidate();
        }
    }

    public void displayWarning(String w) {
        JOptionPane.showMessageDialog(panel, "Warning", w, JOptionPane.WARNING_MESSAGE);
    }

    public void displayError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
}
