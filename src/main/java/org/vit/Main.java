package org.vit;

public class Main {

    static void main() {
        IO.println("Application is starting ...");
        JobQueue queue = new JobQueue();
        SimpleWebServer webServer = new SimpleWebServer(queue);
        webServer.run();
    }

}
