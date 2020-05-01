package net.donotturnoff.simpledoc.browser;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class SDTPURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) {
        return new SDTPURLConnection(url);
    }
}
