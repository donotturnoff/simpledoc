package net.donotturnoff.simpledoc.browser.element;

import net.donotturnoff.simpledoc.browser.ConnectionWorker;
import net.donotturnoff.simpledoc.browser.Page;
import net.donotturnoff.simpledoc.browser.lexing.Token;
import net.donotturnoff.simpledoc.util.ConnectionUtils;
import net.donotturnoff.simpledoc.util.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ImgElement extends BoxElement {

    private JPanel panel;

    public ImgElement(Page page, Map<String, String> attributes, List<Element> children) {
        super(page, "img", attributes, children);
    }

    @Override
    public void render(Page page, JPanel parentPanel) {
        panel = createPanel();
        style(panel);
        parentPanel.add(panel);
        String src = attributes.get("src");
        URL url;
        try {
            url = ConnectionUtils.getURL(page.getUrl(), src);
            load(url);
        } catch (MalformedURLException e) {
            loadingFailure("Failed to load image: " + e.getMessage());
        }
    }

    private void loadingFailure(String s) {
        super.renderChildren(page, panel);
        panel.repaint();
        panel.revalidate();
    }

    private void load(URL url) {
        ConnectionWorker worker = new ConnectionWorker(url, page, this::loaded);
        worker.execute();
    }

    public Void loaded(Response response) {
        byte[] data = response.getBody();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage image;
        try {
            image = ImageIO.read(bais);
            if (image == null) {
                throw new IOException("No data or unrecognised format");
            }
            JLabel picLabel = new JLabel(new ImageIcon(image));
            panel.add(picLabel);
            panel.repaint();
            panel.revalidate();
        } catch (IOException e) {
            loadingFailure("Failed to load image: " + e.getMessage());
        }
        return null;
    }
}
