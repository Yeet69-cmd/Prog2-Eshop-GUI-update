package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestProcessor implements Runnable {

    private final Socket clientSocket;

    public ClientRequestProcessor(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        System.out.println(
                "Client wird verarbeitet von: "
                        + Thread.currentThread().getName()
        );

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        clientSocket.getOutputStream(),
                        true
                )
        ) {
            String anfrage = reader.readLine();

            System.out.println("Anfrage erhalten: " + anfrage);

            if ("PING".equals(anfrage)) {
                writer.println("PONG");
            } else {
                writer.println("FEHLER: Unbekannte Anfrage");
            }

        } catch (IOException e) {
            System.err.println(
                    "Fehler bei der Client-Verarbeitung: "
                            + e.getMessage()
            );
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println(
                        "Client-Socket konnte nicht geschlossen werden."
                );
            }
        }
    }
}