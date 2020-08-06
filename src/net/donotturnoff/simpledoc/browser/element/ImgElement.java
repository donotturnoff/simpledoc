package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.ConnectionWorker;
import net.donotturnoff.simpledoc.browser.JImagePanel;
import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ImgElement extends BoxElement {

    private JImagePanel panel;
    private URL url;

    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    public JImagePanel getPanel() {
        return new JImagePanel();
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        style(panel);
        parentPanel.add(panel);
        String src = attributes.get("src");
        try {
            url = ConnectionUtils.getURL(page.getUrl(), src);
            load();
        } catch (MalformedURLException e) {
            loadingFailure("Failed to load image: " + e.getMessage());
        }
    }

    private void loadingFailure(String s) {
        super.renderChildren(page, panel);
        page.setStatus("Failed to load " + url);
        panel.repaint();
        panel.revalidate();
    }

    private void load() {
        page.setStatus("Loading " + url);
        ConnectionWorker worker = new ConnectionWorker(url, page, this::loaded);
        worker.execute();
    }

    public Void loaded(URL url, Response response) {
        byte[] data = response.getBody();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage img;
        try {
            img = ImageIO.read(bais);
            if (img == null) {
                throw new IOException("No data or unrecognised format");
            }
            panel.setImage(img);
            panel.repaint();
            panel.revalidate();
            page.setStatus("Loaded " + url);
        } catch (IOException e) {
            loadingFailure("Failed to load image: " + e.getMessage());
        }
        return null;
    }
}
