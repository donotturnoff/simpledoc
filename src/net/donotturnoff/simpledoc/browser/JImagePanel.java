package net.donotturnoff.simpledoc.browser;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class JImagePanel extends JPanel {
    private Image img;
    private final JLabel label;
    private final Runnable callback;
    private final Consumer<Exception> errorHandlerCallback;
    private boolean scaled;
    private int pw = 0, ph = 0;

    public JImagePanel(Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = false;
        this.label = new JLabel();
        setBackground(Color.WHITE);
        img = null;
        add(label);
    }

    public JImagePanel(byte[] data, Runnable callback, Consumer<Exception> errorHandlerCallback) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = false;
        this.label = new JLabel();
        setBackground(Color.WHITE);
        setImage(data);
        add(label);
    }

    public JImagePanel(byte[] data, Runnable callback, Consumer<Exception> errorHandlerCallback, boolean scaled) {
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
        this.scaled = scaled;
        this.label = new JLabel();
        setBackground(Color.WHITE);
        setImage(data);
        add(label);
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
        refresh();
    }

    private void refresh() {
        int[] size = getScaledSize();
        setPreferredSize(new Dimension(size[0], size[1]));
        label.setIcon(new ImageIcon(img.getScaledInstance(size[0], size[1], Image.SCALE_DEFAULT)));
        repaint();
        revalidate();
    }

    private int[] getScaledSize() {
        if (scaled) {
            int w = img.getWidth(null);
            int h = img.getHeight(null);
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
            return new int[]{img.getWidth(this), img.getHeight(this)};
        }
    }

    private void setParentDimensions() {
        Component parent = getParent();
        if (parent != null) {
            pw = parent.getWidth();
            ph = parent.getHeight();
        }
    }

    public void rendered(Image img) {
        this.img = img;
        setParentDimensions();

        // Hack to force image to calculate its dimensions so getSize doesn't return {-1, -1}
        label.setIcon(new ImageIcon(img));

        refresh();
        callback.run();
    }
}