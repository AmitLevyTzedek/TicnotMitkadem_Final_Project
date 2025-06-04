package views;

import server.HTTPServer;
import server.MyHTTPServer;
import servlets.TopicDisplayer;
import servlets.ConfLoader;
import servlets.HtmlLoader;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception{
        HTTPServer server=new MyHTTPServer(8080,5);
        //System.out.println("Working dir = " + System.getProperty("user.dir"));
//        File base = new File("html_files");
//        System.out.println("html_files exists? " + base.exists());
//        System.out.println("Contents: " + Arrays.toString(base.list()));


        System.out.println("Server started");
        server.addServlet("GET", "/publish", new TopicDisplayer());
        System.out.println("get publish started");
        server.addServlet("POST", "/upload", new ConfLoader());
        System.out.println("post upload started");
        server.addServlet("GET", "/app/", new HtmlLoader("C:/Users/USER/Desktop/finalProj2025/tichnotmitkadm1/html_files"));
        System.out.println("get app started");
        server.start();
        System.out.println("Server start");
        System.in.read();
        server.close();
        System.out.println("done");
    }
}