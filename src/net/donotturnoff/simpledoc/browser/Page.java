package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;

class Page {

    private final JPanel panel;
    private URL url;
    private Response data;
    private Element root;
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

    public Element getRoot() {
        return root;
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
        parse();
        render();
    }

    private void parse() {

    }

    private void render() {
        panel.removeAll();
        if (data != null) {
            JLabel label = new JLabel(data.getBody());
            panel.add(label);
            panel.repaint();
            panel.revalidate();
        }
    }

    public void displayError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
}
