package servlets;

import server.RequestParser.RequestInfo;
import servlets.Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Serves static HTML files from a directory.
 * Registered against e.g. "GET /app/" so that
 * GET /app/index.html → serves {htmlDir}/index.html
 */
public class HtmlLoader implements Servlet {
    private final File htmlDir;

    /**
     * @param htmlDirPath path on disk where your .html files live
     */
    public HtmlLoader(String htmlDirPath) {
        this.htmlDir = new File(htmlDirPath);
        if (!htmlDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Invalid HTML directory: " + htmlDirPath);
        }
        //System.out.println("DEBUG: HtmlLoader is looking in: " + htmlDir.getPath());
    }

    @Override
    public void handle(RequestInfo request, OutputStream out) throws IOException {
        // only GET
        if (!"GET".equalsIgnoreCase(request.getHttpCommand())) {
            writeStatus(405, "Method Not Allowed", "<h1>405 Method Not Allowed</h1>", out);
            return;
        }


        // extract filename from URI segments: "/app/xyz.html" → segments = ["app","xyz.html"]
        String[] segs = request.getUriSegments();
        String name;
        if (segs.length >= 2) {
            name = segs[1];
        } else {
            // default to index.html if no filename
            name = "index.html";
            System.out.println("index");
        }

        File f = new File(htmlDir, name);
        if (!f.isFile()) {
            writeStatus(404, "Not Found",
                    "<h1>404 Not Found</h1><p>File " + name + " not found.</p>",
                    out);
            System.out.println("file not found");
            return;
        }

        // read the file bytes
        byte[] bytes = new byte[(int) f.length()];
        try (FileInputStream fin = new FileInputStream(f)) {
            int read = fin.read(bytes);
            if (read != bytes.length) {
                // partial read?
                throw new IOException("Could not read full file " + f);
            }
        }

        // write HTTP 200 + headers + body
        String hdr =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + bytes.length + "\r\n" +
                        "\r\n";
        out.write(hdr.getBytes(StandardCharsets.US_ASCII));
        out.write(bytes);
        out.flush();
    }

    private void writeStatus(int code, String reason, String htmlBody, OutputStream out)
            throws IOException
    {
        byte[] b = htmlBody.getBytes(StandardCharsets.UTF_8);
        String hdr =
                "HTTP/1.1 " + code + " " + reason + "\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + b.length + "\r\n" +
                        "\r\n";
        out.write(hdr.getBytes(StandardCharsets.US_ASCII));
        out.write(b);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        // no resources to free
    }
}
