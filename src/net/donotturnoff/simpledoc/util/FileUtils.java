package net.donotturnoff.simpledoc.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private static String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf+1);
    }

    public static String getMime(Path p) throws IOException {
        String ext = getFileExtension(p.toString());
        if (ext.equals("sdml")) {
           return "text/sdml";
        } else if (ext.equals("sdss")) {
           return "text/sdss";
        } else {
            return Files.probeContentType(p);
        }
    }

    public static String getFilename(URL url) {
        String filename;
        try {
            filename = Paths.get(new URI(url.toString()).getPath()).getFileName().toString();
            if (filename == null) {
                filename = url.getFile();
            } else if (filename.isEmpty() || filename.equals("/")) {
                filename = "index.sdml";
            }
        } catch (URISyntaxException e) {
            filename = url.getFile();
        }
        return filename;
    }
}
