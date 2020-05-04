package net.donotturnoff.simpledoc.browser;

import net.donotturnoff.simpledoc.util.Response;

import javax.swing.*;
import java.net.URL;

class Page {

    private JPanel panel;
    private URL url;
    private Response data;
    private Element root;

    Page() {
        this.panel = new JPanel();
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

    public void load(URL url) {
        this.url = url;
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

    public void displayError(Exception e) {
        JOptionPane.showMessageDialog(panel, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
}
