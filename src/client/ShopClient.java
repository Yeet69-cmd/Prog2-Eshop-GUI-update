package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ShopClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public static void main(String[] args) {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(), true
                )
        ) {

            System.out.println("Mit Server verbunden");

            writer.println("PING");

            String antwort = reader.readLine();

            System.out.println("Antwort vom Server: " + antwort);

        } catch (IOException e) {
            System.err.println("Verbindung fehlgeschlagen: "
                    + e.getMessage());
            e.printStackTrace();
        }
    }
}