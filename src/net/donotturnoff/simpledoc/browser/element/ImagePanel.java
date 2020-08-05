package net.donotturnoff.simpledoc.browser.element;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private final BufferedImage img;

    public ImagePanel(BufferedImage img) {
        this.img = img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Paint the background image and scale it appropriately
        int pnlW = getWidth();
        int pnlH = getHeight();
        int w = img.getWidth();
        int h = img.getHeight();
        double aspect = ((double) w)/h;
        if (w > pnlW) {
            w = pnlW;
            h = (int) (w/aspect);
        }
        if (h > pnlH) {
            h = pnlH;
            w = (int) (h*aspect);
        }

        g.drawImage(img, 0, 0, w, h, this);
    }
}
