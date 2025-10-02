package org.vit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.vit.Utils.getCurrentTime;

public class SimpleWebServer {

    private static final int PORT = 8080;
    private static final int BACKLOG = 500; // requested maximum length of the queue of incoming connections

    private final JobQueue queue;
    private final ExecutorService executor;

    public SimpleWebServer(JobQueue queue) {
        this.queue = queue;
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG)) {
            IO.println("Server started at:" + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            try {
                String requestLine = in.readLine();
                if (requestLine == null) {
                    return;
                }
                IO.println("Request: " + requestLine);
                if (requestLine.startsWith("POST /produce ")) {
                    handleProduceRequest(out);
                } else if (requestLine.startsWith("GET /factorial ")) {
                    handleGetFactorialRequest(out);
                } else {
                    handleNotFoundRequest(out);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNotFoundRequest(PrintWriter out) {
        var msg = "<h1>404</h1><p>Page not found</p>";
        sendResponse(out, 404, msg);
    }

    private void handleGetFactorialRequest(PrintWriter out) throws InterruptedException {
        executor.submit(queue.getJob());
        var msg = "The factorial is pulled out of the queue and the calculation is started!";
        sendResponse(out, 200, msg);
    }

    private void handleProduceRequest(PrintWriter out) {
        ClientProducer producer = new ClientProducer(queue);
        executor.submit(producer);
        var msg = "A request for factorial calculation has been sent!";
        sendResponse(out, 200, msg);
    }

    private void sendResponse(PrintWriter out, int statusCode, String body) {
        body = div(body) + div("Current time: " + getCurrentTime());
        String statusLine;
        switch (statusCode) {
            case 200 -> statusLine = "HTTP/1.1 200 OK";
            case 404 -> statusLine = "HTTP/1.1 404 Not Found";
            default -> statusLine = "HTTP/1.1 " + statusCode + " Unknown";
        }

        String response = statusLine + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head><title>Test app</title></head>\n" +
                "<body>" + body + "</body>\n" +
                "</html>";

        out.println(response);
    }

    private String div(String s) {
        return String.format("<div>%s</div>", s);
    }

}
