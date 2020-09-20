package net.donotturnoff.simpledoc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
