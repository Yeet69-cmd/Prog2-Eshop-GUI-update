package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import shared.LoginErgebnis;

public class ShopClient {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public List<String> getArtikel() throws IOException {

        List<String> artikelListe = new ArrayList<>();

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("GET_ARTIKEL");

            String antwort;

            while ((antwort = reader.readLine()) != null) {

                if ("ENDE".equals(antwort)) {
                    break;
                }

                artikelListe.add(antwort);
            }
        }

        return artikelListe;
    }

    public boolean ping() {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("PING");

            return "PONG".equals(reader.readLine());

        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {

        ShopClient client = new ShopClient();

        try {
            for (String artikel : client.getArtikel()) {
                System.out.println(artikel);
            }
        } catch (IOException e) {
            System.err.println(
                    "Verbindung fehlgeschlagen: " + e.getMessage()
            );
        }
    }
    public LoginErgebnis login(
            String benutzername,
            String passwort
    ) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("LOGIN");
            writer.println(benutzername);
            writer.println(passwort);

            String antwort = reader.readLine();

            System.out.println("Login-Antwort: " + antwort);

            if (antwort == null) {
                return new LoginErgebnis(
                        false,
                        null,
                        null,
                        null,
                        "Keine Antwort vom Server"
                );
            }

            String[] teile = antwort.split("\\|", 2);
            String typ = teile[0];
            String inhalt = teile.length > 1 ? teile[1] : "";

            if ("KUNDE".equals(typ)) {
                return new LoginErgebnis(
                        true,
                        "KUNDE",
                        inhalt,
                        benutzername,
                        null
                );
            }

            if ("MITARBEITER".equals(typ)) {
                return new LoginErgebnis(
                        true,
                        "MITARBEITER",
                        inhalt,
                        benutzername,
                        null
                );
            }

            return new LoginErgebnis(
                    false,
                    null,
                    null,
                    null,
                    inhalt.isEmpty() ? "Login fehlgeschlagen" : inhalt
            );
        }
    }
    public String artikelInWarenkorb(String benutzerkennung, int artikelId, int menge
    ) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("WARENKORB_HINZUFUEGEN");
            writer.println(benutzerkennung);
            writer.println(artikelId);
            writer.println(menge);

            String antwort = reader.readLine();

            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            String[] teile = antwort.split("\\|", 2);

            if ("OK".equals(teile[0])) {
                return teile.length > 1
                        ? teile[1]
                        : "Artikel wurde hinzugefügt.";
            }

            throw new IOException(
                    teile.length > 1
                            ? teile[1]
                            : "Artikel konnte nicht hinzugefügt werden"
            );
        }
    }
    public List<String> warenkorbAnzeigen(
            String benutzerkennung
    ) throws IOException {

        List<String> eintraege = new ArrayList<>();

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("WARENKORB_ANZEIGEN");
            writer.println(benutzerkennung);

            String antwort;

            while ((antwort = reader.readLine()) != null) {

                if ("ENDE".equals(antwort)) {
                    break;
                }

                if ("LEER".equals(antwort)) {
                    return eintraege;
                }

                if (antwort.startsWith("FEHLER|")) {
                    throw new IOException(
                            antwort.substring("FEHLER|".length())
                    );
                }

                eintraege.add(antwort);
            }
        }

        return eintraege;
    }
    public List<String> kaufen(
            String benutzerkennung
    ) throws IOException {

        List<String> rechnung = new ArrayList<>();

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("KAUFEN");
            writer.println(benutzerkennung);

            String antwort = reader.readLine();

            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            if (antwort.startsWith("FEHLER|")) {
                throw new IOException(
                        antwort.substring("FEHLER|".length())
                );
            }

            if (!"RECHNUNG".equals(antwort)) {
                throw new IOException(
                        "Ungültige Antwort vom Server: " + antwort
                );
            }

            String zeile;

            while ((zeile = reader.readLine()) != null) {

                if ("ENDE".equals(zeile)) {
                    break;
                }

                rechnung.add(zeile);
            }
        }

        return rechnung;
    }
    public String einlagern(int artikelId, int menge)
            throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("EINLAGERN");
            writer.println(artikelId);
            writer.println(menge);

            String antwort = reader.readLine();

            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            if (antwort.startsWith("FEHLER|")) {
                throw new IOException(antwort.substring("FEHLER|".length()));
            }

            if (antwort.startsWith("OK|")) {
                return antwort.substring("OK|".length());
            }

            throw new IOException(
                    "Ungültige Serverantwort: " + antwort
            );
        }
    }
    public String auslagern(
            int artikelId,
            int menge
    ) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("AUSLAGERN");
            writer.println(artikelId);
            writer.println(menge);
            String antwort = reader.readLine();
            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            if (antwort.startsWith("FEHLER|")) {
                throw new IOException(
                        antwort.substring("FEHLER|".length())
                );
            }

            if (antwort.startsWith("OK|")) {
                return antwort.substring("OK|".length());
            }

            throw new IOException(
                    "Ungültige Serverantwort: " + antwort
            );
        }
    }
    public String artikelAnlegen(
            int id,
            String name,
            int bestand,
            double preis,
            boolean massengut,
            int packungsgroesse
    ) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("ARTIKEL_ANLEGEN");
            writer.println(id);
            writer.println(name);
            writer.println(bestand);
            writer.println(preis);
            writer.println(massengut);
            writer.println(packungsgroesse);
            String antwort = reader.readLine();

            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            if (antwort.startsWith("FEHLER|")) {
                throw new IOException(antwort.substring("FEHLER|".length()));
            }

            if (antwort.startsWith("OK|")) {
                return antwort.substring("OK|".length());
            }

            throw new IOException(
                    "Ungültige Serverantwort: " + antwort
            );
        }
    }
    public String artikelLoeschen(int artikelId) throws IOException {

        try (
                Socket socket = new Socket(HOST, PORT);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {
            writer.println("ARTIKEL_LOESCHEN");
            writer.println(artikelId);

            String antwort = reader.readLine();

            if (antwort == null) {
                throw new IOException("Keine Antwort vom Server");
            }

            if (antwort.startsWith("FEHLER|")) {
                throw new IOException(
                        antwort.substring("FEHLER|".length())
                );
            }

            if (antwort.startsWith("OK|")) {
                return antwort.substring("OK|".length());
            }

            throw new IOException(
                    "Ungültige Serverantwort: " + antwort
            );
        }
    }
}