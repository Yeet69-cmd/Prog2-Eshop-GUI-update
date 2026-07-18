package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ShopServer {

    private static final int PORT = 9999;

    public static void main(String[] args) {

        System.out.println("eShop-Server wird gestartet");

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
                        new ClientRequestProcessor(clientSocket);

                Thread clientThread = new Thread(processor);
                clientThread.start();
            }

        } catch (IOException e) {
            System.err.println("Serverfehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}