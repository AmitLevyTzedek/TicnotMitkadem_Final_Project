package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;


public class RequestParser {

    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {

        String requestLine = reader.readLine();
        System.out.println(requestLine);
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        // Split the first line into parts (method, URL, etc.)
        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            return null;
        }

        String httpCommand = parts[0];
        String fullUri = parts[1];
        System.out.println("dull uri:"+fullUri);

        // Get the path part (without query parameters)
        String uri = fullUri.split("\\?")[0];
        System.out.println("uri:"+uri);
        String[] uriSegments = uri.length() > 1 ? uri.substring(1).split("/") : new String[0];
        System.out.println("uriseg:"+uriSegments);

        // Read query parameters (after '?')
        Map<String, String> parameters = new HashMap<>();
        int queryIndex = fullUri.indexOf("?");
        if (queryIndex != -1) {
            String queryString = fullUri.substring(queryIndex + 1);
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key   = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    parameters.put(key, value);
                    System.out.println("keyValue:"+keyValue[0]+":"+keyValue[1]);
                }
            }
        }

        // Read headers
        Map<String, String> headers = new HashMap<>();
        String boundary = null;
        String line;
        int contentLength = 0;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {

           System.out.println("Debug: Header line: " + line);
            if (line.startsWith("Content-Length:")) {

                contentLength = Integer.parseInt((line.split(":",2)[1]).trim());
                System.out.println("ContentLength:"+contentLength);
            }
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);

                // Check for multipart boundary
                if (key.equalsIgnoreCase("Content-Type") && value.startsWith("multipart/form-data")) {
                    String[] boundaryParts = value.split("boundary=");
                    if (boundaryParts.length > 1) {
                        boundary = "--" + boundaryParts[1].trim();
                      //  System.out.println("Debug: Found boundary: " + boundary);
                    }
                }
            }
        }
        // --- after reading headers, before any parameter/body loops ---
        if (contentLength == 0) {
            // No body on this request (e.g. a GET), so return now.
            return new RequestInfo(
                    httpCommand,
                    uri,
                    uriSegments,
                    parameters,        // only the query‐params so far
                    new byte[0]        // empty body
            );
        }
        // --- end insertion ---



        //ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
        //ByteArrayOutputStream contentStream1 = new ByteArrayOutputStream();
        byte[] contentBytes=null;

        // Handle multipart form data
        if (boundary != null) {
         //   System.out.println("Debug: Processing multipart form data");
            StringBuilder currentContent = new StringBuilder();
            String currentName = null;
            boolean isFile = false;
            boolean isContent = false;

            // Read until we find the first boundary
            while ((line = reader.readLine()) != null && !line.startsWith(boundary)) {
              System.out.println("Debug: Skipping until boundary: " + line);
            }

            while ((line = reader.readLine()) != null) {
              System.out.println("Debug: Reading line: " + line);

                if (line.startsWith(boundary)) {
                    // Save previous content if exists
                    if (currentName != null && currentContent.length() > 0) {
                        String content = currentContent.toString().trim();
                      //  System.out.println("Debug: Saving content for " + currentName + ": " + content);
                        parameters.put(currentName, content);
                    }

                    // Reset for next part
                    currentContent = new StringBuilder();
                    currentName = null;
                    isFile = false;
                    isContent = false;

                    // Check if this is the final boundary
                    if (line.startsWith(boundary + "--")) {
                        break;
                    }
                    continue;
                }

                if (line.startsWith("Content-Disposition:")) {
                 //   System.out.println("Debug: Processing Content-Disposition: " + line);
                    String[] parts2 = line.split(";");
                    for (String part : parts2) {
                        part = part.trim();
                        if (part.startsWith("name=")) {
                            currentName = part.substring(6, part.length() - 1);
                           // System.out.println("Debug: Found field name: " + currentName);
                        }
                        if (part.startsWith("filename=")) {
                            String filename = part.substring(10, part.length() - 1);
                           // System.out.println("Debug: Found filename: " + filename);
                            parameters.put(currentName + "_filename", filename);
                            isFile = true;
                        }
                    }
                    continue;
                }

                if (line.startsWith("Content-Type:")) {
                    continue;
                }

                if (line.isEmpty()) {
                    isContent = true;
                    continue;
                }

                if (isContent) {
                    currentContent.append(line).append("\n");
                }
            }

            // Save last content if exists
            if (currentName != null && currentContent.length() > 0) {
                String content = currentContent.toString().trim();
                //System.out.println("Debug: Saving final content for " + currentName + ": " + content);
                parameters.put(currentName, content);
            }
        } else {
            // 1) Read parameters — each line until the blank line
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                // e.g.: filename="hello_world.txt"
                System.out.println(line);
                String[] kv = line.split("=", 2);
                if (kv.length == 2) {
                    String key = kv[0];
                    String val = kv[1];

                    parameters.put(key, val);
                }
            }
            StringBuilder contentBuilder = new StringBuilder();
            boolean first = true;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {

                contentBuilder.append(line).append('\n');
                first = false;
            }


            contentBytes = contentBuilder.toString()
                    .getBytes(StandardCharsets.UTF_8);


        }

        byte[] content = contentBytes;
        return new RequestInfo(httpCommand, fullUri, uriSegments, parameters, content);
    }


    // RequestInfo given internal class
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
