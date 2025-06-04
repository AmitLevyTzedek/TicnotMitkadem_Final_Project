package servlets;

import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import server.RequestParser.RequestInfo;

public class TopicDisplayer implements Servlet {
    // Routes GET requests to doGet, other methods return 405
    /**
     * Minimal HTML-escaping for text nodes.
     */
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    @Override
    public void handle(RequestInfo request, OutputStream toClient) throws IOException {
        String method = request.getHttpCommand().toUpperCase();
        if (!"GET".equalsIgnoreCase(request.getHttpCommand())) {
            writeStatus(405, "Method Not Allowed", "<h1>405 Method Not Allowed</h1>", toClient);
            return;
        }

        // 1) Extract the two parameters from the request:
        String topic   = request.getParameters().get("topic");
        String message1 = request.getParameters().get("message");


        // 2) If both exist, store/update them in the TopicManager
        if (topic != null && message1 != null) {
            Message message=new Message(message1);
            TopicManagerSingleton.get().getTopic(topic).publish(message);
        }


        Collection<Topic> topics = TopicManagerSingleton.get().getTopics();
        System.out.println("debug:topics"+topics);
        // 4) Gather all topics to render

        // 5) Build the HTML body
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>\n")
                .append("<html><head><meta charset=\"UTF-8\"><title>Agent Calculation Results</title>\n")
                .append("<style>\n")
                .append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }\n")
                .append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }\n")
                .append("th { background-color: #4CAF50; color: white; }\n")
                .append("tr:nth-child(even) { background-color: #f2f2f2; }\n")
                .append("tr:hover { background-color: #ddd; }\n")
                .append(".result { color: #2196F3; font-weight: bold; }\n")
                .append(".input { color: #4CAF50; font-weight: bold; }\n")
                .append(".calculated { color: #FF9800; font-weight: bold; }\n")
                .append("</style></head>\n")
                .append("<body>\n")
                .append("  <h1>Agent Calculation Results</h1>\n")
                .append("  <table>\n")
                .append("    <tr><th>Topic</th><th>Value</th><th>Type</th></tr>\n");

        for (Topic t : topics) {
            String name = t.getName();
            String value = t.getMessage().getString();
            if (value == null || value.isEmpty()) {
                continue; // Skip topics with no value
            }
            // Determine if this is an input or calculated
            String type = "Input";
            if (!t.getpublishers().isEmpty()) {
                type = "Calculated";
            }
            String typeClass = type.equals("Input") ? "input" : "calculated";
            body.append("    <tr>")
                .append("<td>").append(escape(name)).append("</td>")
                .append("<td class=\"result\">").append(escape(value)).append("</td>")
                .append("<td class=\"").append(typeClass).append("\">").append(type).append("</td>")
                .append("</tr>\n");
        }

        body.append("  </table>\n")
            .append("</body></html>\n");

        byte[] bodyBytes = body.toString().getBytes(StandardCharsets.UTF_8);

        // 6) Write the HTTP response
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n")
                .append("Content-Type: text/html; charset=UTF-8\r\n")
                .append("Content-Length: ").append(bodyBytes.length).append("\r\n")
                .append("\r\n");

        toClient.write(header.toString().getBytes(StandardCharsets.UTF_8));
        toClient.write(bodyBytes);
        toClient.flush();
    }
    @Override
    public void close() throws IOException {
        // nothing to clean up
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

//            for (Map.Entry<String, String> entry : topics.entrySet()) {
//                html.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
//                System.out.println("debug:topics"+entry.getKey() +"message"+entry.getValue());
//            }
//
//            html.append("</ul></body></html>");
//            response.write(html.toString());
//
}





