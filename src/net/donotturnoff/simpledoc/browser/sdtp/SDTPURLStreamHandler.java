package net.donotturnoff.simpledoc.browser.sdtp;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class SDTPURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) {
        return new SDTPURLConnection(url);
    }
}
