package server;

import domain.Artikel;
import logic.ShopService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import domain.Benutzer;
import domain.Kunde;
import domain.Mitarbeiter;
import domain.Artikel;
import domain.Kunde;
import domain.*;

public class ClientRequestProcessor implements Runnable {

    private final Socket clientSocket;
    private final ShopService shopService;

    public ClientRequestProcessor(
            Socket clientSocket,
            ShopService shopService
    ) {
        this.clientSocket = clientSocket;
        this.shopService = shopService;
    }

    @Override
    public void run() {

        System.out.println(
                "Client wird verarbeitet von: "
                        + Thread.currentThread().getName()
        );

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()
                        )
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

            } else if ("GET_ARTIKEL".equals(anfrage)) {

                artikelSenden(writer);

            } else if ("LOGIN".equals(anfrage)) {

                loginVerarbeiten(reader, writer);
            } else if ("WARENKORB_HINZUFUEGEN".equals(anfrage)) {

                warenkorbHinzufuegen(reader, writer);
            } else if ("WARENKORB_ANZEIGEN".equals(anfrage)) {

                warenkorbAnzeigen(reader, writer);

            } else if ("KAUFEN".equals(anfrage)) {
                kaufen(reader, writer);

            } else if ("EINLAGERN".equals(anfrage)) {

                einlagern(reader, writer);
            } else if ("AUSLAGERN".equals(anfrage)) {

                auslagern(reader, writer);
            } else if ("ARTIKEL_ANLEGEN".equals(anfrage)) {

                artikelAnlegen(reader, writer);
            } else if ("ARTIKEL_LOESCHEN".equals(anfrage)) {

                artikelLoeschen(reader, writer);
            } else if ("KUNDE_REGISTRIEREN".equals(anfrage)) {

                kundeRegistrieren(reader, writer);

            } else if ("MITARBEITER_REGISTRIEREN".equals(anfrage)) {

                mitarbeiterRegistrieren(reader, writer);

            } else if ("GET_EREIGNISSE".equals(anfrage)) {

                ereignisseSenden(writer);
            } else if ("GET_BESTANDSHISTORIE".equals(anfrage)) {

                bestandsHistorieSenden(reader, writer);
            }
            else {

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

    private void artikelSenden(PrintWriter writer) {

        synchronized (shopService) {
            for (Artikel artikel : shopService.getArtikelList()) {
                writer.println(
                        artikel.getArtikelId() + "|" + artikel.getName() + "|" + artikel.getBestand() + "|"
                                + artikel.getPreis()
                );
            }
        }
        writer.println("ENDE");
    }
    private void loginVerarbeiten(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String benutzername = reader.readLine();
        String passwort = reader.readLine();

        try {
            Benutzer benutzer = shopService.login(
                    benutzername,
                    passwort
            );

            System.out.println(
                    "Login-Typ: " + benutzer.getClass().getSimpleName()
            );
            String name = benutzer.getName();

            if (name == null || name.isBlank()) {
                name = benutzername;
            }
            if (benutzer instanceof Kunde) {
                writer.println("KUNDE|" + name);

            } else if (benutzer instanceof Mitarbeiter) {
                writer.println("MITARBEITER|" + name);

            } else {
                writer.println("FEHLER|Unbekannter Benutzertyp");
            }

        } catch (Exception e) {
            writer.println("FEHLER|" + e.getMessage());
        }
    }
    private void warenkorbHinzufuegen(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String benutzerkennung = reader.readLine();
        int artikelId = Integer.parseInt(reader.readLine());
        int menge = Integer.parseInt(reader.readLine());

        Kunde kunde = shopService.findeKunde(benutzerkennung);
        Artikel artikel = shopService.findeArtikel(artikelId);

        if (kunde == null) {
            writer.println("FEHLER|Kunde nicht gefunden");
            return;
        }

        if (artikel == null) {
            writer.println("FEHLER|Artikel nicht gefunden");
            return;
        }

        if (menge <= 0) {
            writer.println("FEHLER|Menge muss größer als 0 sein");
            return;
        }

        kunde.getWarenkorb().addArtikel(artikel, menge);

        writer.println(
                "OK|" + artikel.getName()
                        + " x " + menge
                        + " wurde in den Warenkorb gelegt."
        );
    }
    private void warenkorbAnzeigen(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String benutzerkennung = reader.readLine();

        Kunde kunde = shopService.findeKunde(benutzerkennung);

        if (kunde == null) {
            writer.println("FEHLER|Kunde nicht gefunden");
            return;
        }

        if (kunde.getWarenkorb().getEintraege().isEmpty()) {
            writer.println("LEER");
            return;
        }

        for (WarenkorbEintrag eintrag :
                kunde.getWarenkorb().getEintraege()) {

            writer.println(
                    eintrag.getArtikel().getName()
                            + " x "
                            + eintrag.getMenge()
            );
        }

        writer.println("ENDE");
    }
    private void kaufen(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String benutzerkennung = reader.readLine();

        Kunde kunde = shopService.findeKunde(benutzerkennung);

        if (kunde == null) {
            writer.println("FEHLER|Kunde nicht gefunden");
            return;
        }

        if (kunde.getWarenkorb().getEintraege().isEmpty()) {
            writer.println("FEHLER|Warenkorb ist leer");
            return;
        }

        try {
            Rechnung rechnung;

            /*
              Verhindert, dass zwei Client-Threads gleichzeitig denselben Lagerbestand verändern
             */
            synchronized (shopService) {
                rechnung = shopService.kaufen(kunde);
                shopService.speichern();
            }

            writer.println("RECHNUNG");
            writer.println("Kunde: " + rechnung.getKunde().getName());
            writer.println("Datum: " + rechnung.getDatum());
            writer.println("");

            for (WarenkorbEintrag eintrag :
                    rechnung.getWarenkorbList()) {

                writer.println(
                        eintrag.getArtikel().getName() + " x " + eintrag.getMenge() + " = " + eintrag.getArtikel().getPreis()
                                * eintrag.getMenge() + " €");
            }

            writer.println("");
            writer.println("Gesamtpreis: " + rechnung.getGesamtpreis() + " €");
            writer.println("ENDE");

        } catch (Exception e) {
            writer.println("FEHLER|" + e.getMessage());
        }
    }
    private void einlagern(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        try {
            int artikelId = Integer.parseInt(reader.readLine());
            int menge = Integer.parseInt(reader.readLine());

            if (menge <= 0) {
                writer.println("FEHLER|Die Menge muss größer als 0 sein");
                return;
            }

            synchronized (shopService) {
                shopService.einlagern(artikelId, menge);
            }

            writer.println("OK|Einlagerung erfolgreich");

        } catch (NumberFormatException e) {
            writer.println("FEHLER|Artikel-ID und Menge müssen Zahlen sein");

        } catch (Exception e) {
            writer.println("FEHLER|" + e.getMessage());
        }
    }
    private void auslagern(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        try {
            int artikelId = Integer.parseInt(reader.readLine());
            int menge = Integer.parseInt(reader.readLine());

            if (menge <= 0) {
                writer.println("FEHLER|Die Menge muss größer als 0 sein");
                return;
            }

            synchronized (shopService) {
                shopService.auslagern(artikelId, menge);
            }

            writer.println("OK|Auslagerung erfolgreich");

        } catch (NumberFormatException e) {
            writer.println("FEHLER|Artikel-ID und Menge müssen Zahlen sein");

        } catch (Exception e) {
            writer.println("FEHLER|" + e.getMessage());
        }
    }
    private void artikelAnlegen(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        try {
            int id = Integer.parseInt(reader.readLine());
            String name = reader.readLine();
            int bestand = Integer.parseInt(reader.readLine());
            double preis = Double.parseDouble(reader.readLine());
            boolean massengut = Boolean.parseBoolean(reader.readLine());
            int packungsgroesse = Integer.parseInt(reader.readLine());

            if (name == null || name.isBlank()) {
                writer.println("FEHLER|Der Artikelname darf nicht leer sein");
                return;
            }

            if (bestand < 0) {
                writer.println("FEHLER|Der Bestand darf nicht negativ sein");
                return;
            }

            if (preis < 0) {
                writer.println("FEHLER|Der Preis darf nicht negativ sein");
                return;
            }

            Artikel artikel;

            if (massengut) {
                if (packungsgroesse <= 0) {
                    writer.println(
                            "FEHLER|Die Packungsgröße muss größer als 0 sein"
                    );
                    return;
                }

                if (bestand % packungsgroesse != 0) {
                    writer.println(
                            "FEHLER|Der Bestand muss ein Vielfaches der Packungsgröße sein"
                    );
                    return;
                }
                artikel = new Massengutartikel(id, name, bestand, preis, packungsgroesse
                );

            } else {
                artikel = new Artikel(id, name, bestand, preis
                );
            }

            synchronized (shopService) {
                shopService.addArtikel(artikel);
                shopService.speichern();
            }
            writer.println("OK|Artikel wurde erfolgreich angelegt");

        } catch (NumberFormatException e) {
            writer.println(
                    "FEHLER|ID, Bestand, Preis und Packungsgröße müssen gültige Zahlen sein"
            );

        } catch (Exception e) {
            String meldung = e.getMessage();
            if (meldung == null || meldung.isBlank()) {
                meldung = e.getClass().getSimpleName();
            }
            writer.println("FEHLER|" + meldung);
        }
    }
    private void artikelLoeschen(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        try {
            int artikelId = Integer.parseInt(reader.readLine());

            synchronized (shopService) {
                shopService.artikelLoeschen(artikelId);
                shopService.speichern();
            }

            writer.println("OK|Artikel wurde erfolgreich gelöscht");

        } catch (NumberFormatException e) {
            writer.println("FEHLER|Die Artikel-ID muss eine Zahl sein");

        } catch (Exception e) {
            String meldung = e.getMessage();

            if (meldung == null || meldung.isBlank()) {
                meldung = e.getClass().getSimpleName();
            }

            writer.println("FEHLER|" + meldung);
        }
    }
    private void kundeRegistrieren(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String name = reader.readLine();
        String adresse = reader.readLine();
        String benutzerkennung = reader.readLine();
        String passwort = reader.readLine();

        if (name == null || name.isBlank()) {
            writer.println("FEHLER|Name darf nicht leer sein");
            return;
        }

        if (adresse == null || adresse.isBlank()) {
            writer.println("FEHLER|Adresse darf nicht leer sein");
            return;
        }

        if (benutzerkennung == null || benutzerkennung.isBlank()) {
            writer.println("FEHLER|Benutzerkennung darf nicht leer sein");
            return;
        }

        if (passwort == null || passwort.isBlank()) {
            writer.println("FEHLER|Passwort darf nicht leer sein");
            return;
        }

        synchronized (shopService) {

            if (shopService.benutzerkennungExistiert(
                    benutzerkennung
            )) {
                writer.println("FEHLER|Benutzerkennung existiert bereits");
                return;
            }

            Kunde kunde = new Kunde(
                    shopService.getNeuBenutzerId(),
                    name.trim(),
                    adresse.trim(),
                    benutzerkennung.trim(),
                    passwort
            );

            shopService.kundeRegistrieren(kunde);
        }

        writer.println("OK|Kunde wurde registriert");
    }
    private void mitarbeiterRegistrieren(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String name = reader.readLine();
        String benutzerkennung = reader.readLine();
        String passwort = reader.readLine();

        if (name == null || name.isBlank()) {
            writer.println("FEHLER|Name darf nicht leer sein");
            return;
        }

        if (benutzerkennung == null || benutzerkennung.isBlank()) {
            writer.println("FEHLER|Benutzerkennung darf nicht leer sein");
            return;
        }

        if (passwort == null || passwort.isBlank()) {
            writer.println("FEHLER|Passwort darf nicht leer sein");
            return;
        }

        synchronized (shopService) {

            if (shopService.benutzerkennungExistiert(benutzerkennung))
            {
                writer.println("FEHLER|Benutzerkennung existiert bereits");
                return;
            }

            Mitarbeiter mitarbeiter = new Mitarbeiter(
                    shopService.getNeuBenutzerId(),
                    name.trim(),
                    benutzerkennung.trim(),
                    passwort
            );

            shopService.mitarbeiterRegistrieren(mitarbeiter);
        }

        writer.println("OK|Mitarbeiter wurde registriert");
    }
    private void ereignisseSenden(PrintWriter writer) {

        synchronized (shopService) {

            if (shopService.getEreignisse().isEmpty()) {
                writer.println("ENDE");
                return;
            }

            for (LagerEreignis ereignis :
                    shopService.getEreignisse()) {

                writer.println(ereignis.toString());
            }
        }

        writer.println("ENDE");
    }
    private void bestandsHistorieSenden(
            BufferedReader reader,
            PrintWriter writer
    ) throws IOException {

        String idText = reader.readLine();

        try {
            int artikelId = Integer.parseInt(idText);

            synchronized (shopService) {

                Artikel artikel = shopService.findeArtikel(artikelId);

                if (artikel == null) {
                    writer.println("FEHLER|Artikel nicht gefunden");
                    writer.println("ENDE");
                    return;
                }

                List<Integer> historie =
                        shopService.getBestandsHistorie(artikel);

                for (Integer bestand : historie) {
                    writer.println(bestand);
                }
            }

            writer.println("ENDE");

        } catch (NumberFormatException e) {
            writer.println("FEHLER|Ungültige Artikel-ID");
            writer.println("ENDE");
        }
    }
}