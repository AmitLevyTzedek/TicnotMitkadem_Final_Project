package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.SocketException;

import server.RequestParser.RequestInfo;
import servlets.Servlet;


public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean running = true;
    private final Map<String, Servlet> servlets = new HashMap<>();

    // Creates server with port and thread pool size
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    // Adds a handler for a specific HTTP method and path
    public void addServlet(String httpCommand, String uri, Servlet s) {
        String key = httpCommand + " " + uri;
        servlets.put(key, s);
        System.out.println("added servlet: " + key);
       // System.out.println("Debug: Added servlet for " + httpCommand.toUpperCase() + " " + uri);
    }

    // Removes a handler for a specific HTTP method and path
    public void removeServlet(String httpCommand, String uri) {
        String key = httpCommand + " " + uri;
        servlets.remove(key);
        System.out.println("removed servlet: " + key);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
           // System.out.println("Server started on port " + port);

            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    threadPool.execute(() -> handleClient(client));

                } catch (java.net.SocketException se) {
                    // This exception is expected when serverSocket.close() is called
                    if (!running) {
                        // normal shutdown: break out of the while loop
                        break;
                    } else {
                        // an unexpected SocketException
                        throw se;
                    }
                }
            }
        } catch (IOException e) {
            // Handles both the ServerSocket constructor and unexpected accept() errors
            e.printStackTrace();
        } finally {
            // Cleanup
            if (serverSocket != null && !serverSocket.isClosed()) {
                try { serverSocket.close(); } catch (IOException ignored) {}
            }
            threadPool.shutdown();
        }
    }


    /**
     * Parse the request, dispatch to the matching Servlet, or return 404.
     */
    private void handleClient(Socket client) {
        // set a 1 s timeout so readLine() won’t block forever
//        try {
//            client.setSoTimeout(1000);
//        } catch (SocketException e) {
//            // if we can’t set the timeout, just log and continue
//            e.printStackTrace();
//        }
        System.out.println("here");
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out = client.getOutputStream();


        ) {
            System.out.println("here2");
            RequestInfo info = RequestParser.parseRequest(reader);
            if (info == null) {
                client.close();

                return;
            }
            System.out.println("here3");

            // Build lookup key: e.g. "GET /params"

            String key = info.getHttpCommand() + " " + info.getUri();
            System.out.println("key: " + key);
            //Servlet servlet = servlets.get(key);
            Servlet servlet = findServlet(info.getHttpCommand(), info.getUri());
            System.out.println("here4");

            if (servlet != null) {
                servlet.handle(info, out);
                //System.out.println("null");
            } else {
                System.out.println("error");
                // 404 Not Found
                String resp = ""
                        + "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Length: 0\r\n"
                        + "\r\n";
                out.write(resp.getBytes());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }
    // Finds the right handler for a request
    private Servlet findServlet(String method, String uri) {
        String upperMethod = method.toUpperCase();
        // נסיון Exact
        String exactKey = upperMethod + " " + uri;
        Servlet best = servlets.get(exactKey);
        if (best != null) return best;

        // נסיון Longest-Match
        int bestLen = -1;
        for (Map.Entry<String, Servlet> e : servlets.entrySet()) {
            String key = e.getKey();
            if (!key.startsWith(upperMethod + " ")) continue;
            String path = key.substring(upperMethod.length() + 1); // מחלק אחרי ה־"METHOD "

            boolean match = uri.startsWith(path) || uri.endsWith(path);
            if (match && path.length() > bestLen) {
                bestLen = path.length();
                best = e.getValue();
            }
        }
        return best;
    }

    // Stops the server and closes all connections
    public void close() {
        running = false;
        threadPool.shutdown();  // Stop accepting new tasks

        try {
            if (serverSocket != null) {
                serverSocket.close();  // Close the server socket
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
