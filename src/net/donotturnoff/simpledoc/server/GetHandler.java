package net.donotturnoff.simpledoc.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

class GetHandler {
    static Response handle(Request r) throws RequestHandlingException {
        try {
            String docPath = r.getPath();
            docPath = docPath.replaceAll("\\.\\.", "");
            LinkOption[] opts = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
            if (SDTPServer.config.getProperty("follow_symlinks").equals("yes")) {
                opts = new LinkOption[]{};
            }
            Path fullPath = Path.of(SDTPServer.config.getProperty("docroot") + docPath).toRealPath(opts);
            File f = fullPath.toFile();
            return (f.isDirectory()) ? handleDirectory(fullPath, docPath) : handleFile(fullPath);
        } catch (NoSuchFileException e) {
            throw new RequestHandlingException(Status.NOT_FOUND, e.getMessage());
        } catch (AccessDeniedException e) {
            throw new RequestHandlingException(Status.FORBIDDEN, e.getMessage());
        } catch (IOException e) {
            throw new RequestHandlingException(Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private static Response handleDirectory(Path p, String docPath) throws IOException {
        String[] indexFiles = SDTPServer.config.getProperty("index_pages").split("\\s,\\s");
        boolean listdir = SDTPServer.config.getProperty("listdir").equals("yes");

        for (String index: indexFiles) {
            Path p2 = Path.of(p.toString(), index);
            try {
                return handleFile(p2);
            } catch (IOException ignored) {}
        }

        if (listdir) {
            return handleDirectoryListing(p, docPath);
        } else {
            throw new AccessDeniedException("Directory listing forbidden");
        }
    }

    private static Response handleDirectoryListing(Path p, String docPath) throws IOException {
        File dir = p.toFile();
        File[] files;
        try {
            files = dir.listFiles();
        } catch (NullPointerException e) {
            throw new FileNotFoundException(e.getMessage());
        }

        String protocol = SDTPServer.DEFAULT_PROTOCOL;
        Status status = Status.OK;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("type", "text/sdml");

        StringBuilder sb = new StringBuilder();
        sb.append("doctype(SDML/1.0)\n\n");
        sb.append("doc(charset=\"UTF-8\"\n");
        sb.append(" head {\n");
        sb.append("  title {\"Directory listing\"}\n");
        sb.append(" }\n");
        sb.append(" body {\n");
        sb.append("  h1 {\"Directory listing\"}\n");
        sb.append("  ul {\n");

        assert files != null;
        for (File f: files) {
            sb.append("   li {\n");
            sb.append("    a (href=\"");
            sb.append(docPath).append("/").append(f.getName());
            sb.append("\") {\"");
            sb.append(f.getName());
            sb.append("\"}\n");
            sb.append("   }\n");
        }

        sb.append("  }\n");
        sb.append(" }\n");
        sb.append("}\n");

        return new Response(protocol, status, headers, sb.toString());
    }

    private static Response handleFile(Path p) throws IOException {
        String body = new String(Files.readAllBytes(p));
        String protocol = SDTPServer.DEFAULT_PROTOCOL;
        Status status = Status.OK;
        HashMap<String, String> headers = new HashMap<>();
        if (body.matches("^doctype\\(SDML/(\\d+)\\.(\\d+)\\)")) {
            headers.put("type", "text/sdml");
        } else {
            String mime = Files.probeContentType(p);
            mime = (mime == null) ? SDTPServer.config.getProperty("default_mime") : mime;
            headers.put("type", mime);
        }
        return new Response(protocol, status, headers, body);
    }
}
