package servlets;

import server.RequestParser.RequestInfo;
import servlets.Servlet;
import views.HtmlGraphWriter;
import configs.GenericConfig;
import graph.Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfLoader implements Servlet {
    private static final String uploadDir = "uploads";

    public ConfLoader() {
       // this.uploadDir = uploadDir;
        // ensure the directory exists
        File d = new File(uploadDir);
        if (!d.exists() && !d.mkdirs()) {
            System.err.println("ConfLoader: could not create uploadDir " + uploadDir);
        }
    }

    @Override
    public void handle(RequestInfo request, OutputStream toClient) throws IOException {
        // 1) grab filename + file contents from the RequestInfo parameters
        String fileName    = request.getParameters().get("configFile_filename");
        String fileContent = request.getParameters().get("configFile");
        if (!"POST".equalsIgnoreCase(request.getHttpCommand())) {
            writeStatus(405, "Method Not Allowed", "<h1>405 Method Not Allowed</h1>", toClient);
            return;
        }

        if (fileName == null || fileContent == null) {
            writeError(400, "Missing upload", toClient);
            return;
        }

        // 2) save to disk
        File dest = new File(uploadDir, fileName);
        try (FileWriter fw = new FileWriter(dest)) {
            fw.write(fileContent);
        }

        // 3) load your GenericConfig from that file
        GenericConfig config = new GenericConfig();
        config.setConfFile(dest.getAbsolutePath());
        config.create();

        // 4) build your computation Graph
        Graph graph = new Graph();
        graph.createFromTopics();  // or whichever Graph ctor / factory you have

        // 5) get back a full HTML page
        List<String> htmlLines = HtmlGraphWriter.getGraphHTML(graph);
        String html = String.join("\n", htmlLines);
        byte[] body = html.getBytes(StandardCharsets.UTF_8);

        // 6) write a valid HTTP response
        String headers =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + body.length + "\r\n" +
                        "\r\n";

        toClient.write(headers.getBytes(StandardCharsets.US_ASCII));
        toClient.write(body);
        toClient.flush();
    }

    private void writeError(int status, String msg, OutputStream out) throws IOException {
        String html =
                "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Error</title></head>" +
                        "<body><h1>" + status + " " + msg + "</h1></body></html>";
        byte[] b = html.getBytes(StandardCharsets.UTF_8);
        String hdr =
                "HTTP/1.1 " + status + " " + msg + "\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + b.length + "\r\n" +
                        "\r\n";
        out.write(hdr.getBytes(StandardCharsets.US_ASCII));
        out.write(b);
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
