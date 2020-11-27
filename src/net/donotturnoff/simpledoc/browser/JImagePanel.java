package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class JImagePanel extends JPanel {
    private BufferedImage img;
    private final Runnable callback;
    private final Consumer<Exception> errorHandlerCallback;
    private boolean scaled;
    private int pw = 0, ph = 0;

    public JImagePanel(Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = false;
        setBackground(Color.WHITE);
        img = null;
    }

    public JImagePanel(byte[] data, Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = false;
        setBackground(Color.WHITE);
        setImage(data);
    }

    public JImagePanel(byte[] data, Runnable callback, Consumer<Exception> errorHandlerCallback, boolean scaled) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = scaled;
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

    public void toggleScaled() {
        scaled = !scaled;
        int[] size = getScaledSize();
        setPreferredSize(new Dimension(size[0], size[1]));
        repaint();
        revalidate();
    }

    private int[] getScaledSize() {
        if (scaled) {
            int w = img.getWidth();
            int h = img.getHeight();
            double ratio = ((double) h)/w;
            if (h > ph) {
                h = ph-10;
                w = (int) (h/ratio);
            }
            if (w > pw) {
                w = pw;
                h = (int) (w*ratio);
            }
            return new int[]{w, h};
        } else {
            return new int[]{img.getWidth(), img.getHeight()};
        }
    }

    private void setParentDimensions() {
        Component parent = getParent();
        if (parent != null) {
            pw = parent.getWidth();
            ph = parent.getHeight();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            int[] size = getScaledSize();
            // Paint the background image
            g.drawImage(img, 0, 0, size[0], size[1], this);
        }
    }

    public void rendered(BufferedImage img) {
        this.img = img;
        setParentDimensions();
        int[] size = getScaledSize();
        setPreferredSize(new Dimension(size[0], size[1]));
        repaint();
        revalidate();
        callback.run();
    }
}