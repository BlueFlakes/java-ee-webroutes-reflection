package webAnno;

import com.sun.net.httpserver.HttpServer;
import webAnno.handlers.MainRoute;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        final int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new MainRoute());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started!");
    }
}
