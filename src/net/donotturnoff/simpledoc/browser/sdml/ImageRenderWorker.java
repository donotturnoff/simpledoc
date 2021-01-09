package net.donotturnoff.simpledoc.browser.sdml;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

public class ImageRenderWorker extends SwingWorker<Image, Void> {

    private final byte[] data;
    private final Consumer<Image> callback;
    private final Consumer<Exception> errorHandlerCallback;
    private Exception e;

    public ImageRenderWorker(byte[] data, Consumer<Image> callback, Consumer<Exception> errorHandlerCallback) {
        this.data = data;
        this.callback = callback;
        this.errorHandlerCallback = errorHandlerCallback;
    }

    @Override
    protected Image doInBackground() {
        Image img = null;
        try {
            img = Toolkit.getDefaultToolkit().createImage(data);
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
