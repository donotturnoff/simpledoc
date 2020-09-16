package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImagePanel extends JPanel {
    private BufferedImage img;

    public JImagePanel() {}

    public JImagePanel(BufferedImage img) {
        setImage(img);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void setImage(BufferedImage img) {
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
    }
}