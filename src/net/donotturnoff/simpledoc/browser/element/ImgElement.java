package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.ConnectionWorker;
import net.donotturnoff.simpledoc.browser.JImagePanel;
import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.SDTPException;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;
import net.donotturnoff.simpledoc.util.Status;

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

    private URL url;

    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    public JImagePanel getPanel() {
        if (isHidden()) {
            return null;
        } else {
            JImagePanel panel = new JImagePanel();
            panel.addMouseListener(this);
            return panel;
        }
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = getPanel();
        style(panel);
        addPanel(parentPanel, panel);
        if (panel != null) {
            String src = attributes.get("src");
            try {
                url = ConnectionUtils.getURL(page.getUrl(), src);
                load();
            } catch (MalformedURLException e) {
                loadingFailure("Failed to load image", e);
            }
        }
    }

    private void loadingFailure(String s) {
        super.renderChildren(page, panel);
        page.warning(s);
        panel.repaint();
        panel.revalidate();
    }

    public Void loadingFailure(String s, Exception e) {
        loadingFailure(s + ": " + e.getMessage());
        return null;
    }

    private void load() {
        page.addPendingResource(url);
        ConnectionWorker worker = new ConnectionWorker(url, page, this::loaded, this::loadingFailure);
        worker.execute();
    }

    public Void loaded(URL url, Response response) {
        Status s = response.getStatus();
        if (s == Status.OK) {
            byte[] data = response.getBody();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            BufferedImage img;
            try {
                img = ImageIO.read(bais);
                if (img == null) {
                    throw new IOException("No data or unrecognised format");
                }
                ((JImagePanel) panel).setImage(img);
                panel.repaint();
                panel.revalidate();
                page.info("Loaded " + url + ": " + response.getStatus());
                page.removePendingResource(url, response);
            } catch (IOException e) {
                loadingFailure("Failed to load " + url, e);
            }
        } else {
            loadingFailure("Failed to load " + url, new SDTPException(s));
        }
        return null;
    }
}
