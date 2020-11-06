package net.donotturnoff.simpledoc.browser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

public class ImageRenderWorker extends SwingWorker<BufferedImage, Void> {

    private final byte[] data;
    private final Consumer<BufferedImage> callback;
    private final Consumer<Exception> errorHandlerCallback;
    private Exception e;

    public ImageRenderWorker(byte[] data, Consumer<BufferedImage> callback, Consumer<Exception> errorHandlerCallback) {
        this.data = data;
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
    }

    @Override
    protected BufferedImage doInBackground() {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedImage img = null;
        try {
            img = ImageIO.read(bais);
            if (img == null) {
                throw new IOException("No data or unrecognised format");
            }
        } catch (IOException e) {
            this.e = e;
        }
        return img;
    }

    @Override
    protected void done() {
        try {
            if (e == null) {
                callback.accept(get());
            } else {
                throw e;
            }
        } catch (Exception e) {
            errorHandlerCallback.accept(e);
        }
    }
}
