package net.donotturnoff.simpledoc.browser;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class SDTPURLStreamHandlerFactory implements URLStreamHandlerFactory {
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (protocol.equals("sdtp")) {
            return new SDTPURLStreamHandler();
        } else {
            return null;
        }
    }
}
