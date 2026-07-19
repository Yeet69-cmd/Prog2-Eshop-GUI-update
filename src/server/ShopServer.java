package server;

import logic.ShopService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import logic.*;

public class ShopServer {

    private static final int PORT = 9999;

    // One shared ShopService for all connected clients
    private static final ShopService shopService = new ShopService();

    public static void main(String[] args) {

        System.out.println("eShop-Server wird gestartet");

        // The server loads the saved shop data
        shopService.laden();

        System.out.println(
                shopService.getArtikelList().size()
                        + " Artikel wurden geladen."
        );

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server läuft auf Port " + PORT);

            while (true) {
                System.out.println("Warte auf einen Client");

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client verbunden: "
                                + clientSocket.getInetAddress()
                );

                ClientRequestProcessor processor =
                        new ClientRequestProcessor(
                                clientSocket,
                                shopService
                        );

                Thread clientThread = new Thread(processor);
                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Serverfehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}