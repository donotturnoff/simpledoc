package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import java.net.URL;

class Page {

    private JPanel panel;
    private URL url;
    private Response data;
    private Element root;

    Page(URL url) {
        this.panel = new JPanel();
        this.url = url;
    }

    public void setURL(URL url) {
        this.url = url;
    }

    public void setData(Response data) {
        this.data = data;
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

    public void load() {
        ConnectionWorker worker = new ConnectionWorker(this);
        worker.execute();
    }

    public void parse() {

    }

    public void render() {
        panel.removeAll();
        if (data != null) {
            JLabel label = new JLabel(data.getBody());
            panel.add(label);
            panel.repaint();
            panel.revalidate();
        }
    }
}
