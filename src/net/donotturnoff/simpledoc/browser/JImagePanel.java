package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImagePanel extends JPanel {
    private BufferedImage img;

public class ImagePanel extends JPanel {
    private final BufferedImage img;

    public ImagePanel(BufferedImage img) {
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            // Paint the background image
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
        }
        if (h > pnlH) {
            h = pnlH;
            w = (int) (h*aspect);
        }

        g.drawImage(img, 0, 0, w, h, this);
    }
}
