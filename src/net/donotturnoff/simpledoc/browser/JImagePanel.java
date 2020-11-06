package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class JImagePanel extends JPanel {
    private BufferedImage img;
    private final Runnable callback;
    private final Consumer<Exception> errorHandlerCallback;

    public JImagePanel(Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        setBackground(Color.WHITE);
        img = null;
    }

    public JImagePanel(byte[] data, Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        setBackground(Color.WHITE);
        setImage(data);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public void setImage(byte[] data) {
        ImageRenderWorker worker = new ImageRenderWorker(data, this::rendered, errorHandlerCallback);
        worker.execute();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            // Paint the background image
            g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), this);
        }
    }

    public void rendered(BufferedImage img) {
        this.img = img;
        setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        repaint();
        revalidate();
        callback.run();
    }
}