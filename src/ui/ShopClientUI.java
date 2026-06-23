package ui;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

import domain.Kunde;
import domain.*;
import logic.ShopService;
import domain.exceptions.ArtikelExistiertBereitsException;
import domain.exceptions.NichtGenugBestandException;
import domain.exceptions.ArtikelExistiertNichtException;
import domain.exceptions.LoginFehlgeschlagenException;

public class ShopClientUI {
    private ShopService shop;
    private BufferedReader in;
    private Kunde aktuellerKunde;
    private Benutzer eingeloggterBenutzer;


    public ShopClientUI() {
        shop=new ShopService();
        in=new BufferedReader(new InputStreamReader(System.in));
        shop.laden();
        if(!shop.hatArtikel()) {
            Artikel a1 = new Artikel(1, "Cola", 10, 2.5);
            Artikel a2 = new Artikel(2, "Chips", 5, 1.5);
            try {
                shop.addArtikel(a1);
                shop.addArtikel(a2);
            } catch (ArtikelExistiertBereitsException e) {
                System.out.println(e.getMessage());
            }
            aktuellerKunde = new Kunde(1, "Moe", "Bremerhaven", "Moe1", "Moeeee66");
            shop.kundeRegistrieren(aktuellerKunde);
            Mitarbeiter admin = new Mitarbeiter(1, "Admin", "admin", "1234");
            shop.mitarbeiterRegistrieren(admin);
        }


    }
    private void kundenMenue() throws IOException {
        String input = "";

        do {
            System.out.println("=== Kundenmenü ===");
            System.out.println("a = Artikel anzeigen");
            System.out.println("h = Artikel in Warenkorb legen");
            System.out.println("w = Warenkorb anzeigen");
            System.out.println("m = Menge ändern");
            System.out.println("k = Kaufen");
            System.out.println("q = Logout");
            System.out.print("> ");
            input = liesEingabe();
            switch (input) {
                case "a" -> artikelAnzeigen();
                case "h" -> artikelInWarenkorbLegen();
                case "w" -> warenkorbAnzeigen();
                case "m" -> mengeAendern();
                case "k" -> kaufen();
                case "q" -> System.out.println("Logout.");
            }
        } while (!input.equals("q"));
    }
    private void mitarbeiterMenue() throws IOException {
        String input = "";

        do {
            System.out.println("=== Mitarbeitermenü ===");
            System.out.println("a = Artikel anzeigen");
            System.out.println("n = Neuer Artikel");
            System.out.println("d = Artikel löschen");
            System.out.println("b = Bestand ändern");
            System.out.println("e = Ereignisse anzeigen");
            System.out.println("q = Logout");
            System.out.print("> ");
            input = liesEingabe();
            switch (input) {
                case "a" -> artikelAnzeigen();
                case "n" -> neuerArtikel();
                case "d" -> artikelLoeschen();
                case "b" -> bestandAendern();
                case "e" -> ereignisseAnzeigen();
                case "q" -> System.out.println("Logout.");
            }
        } while (!input.equals("q"));
    }
    private void startMenue() {
        System.out.println("=== eShop ===");
        System.out.println("1 = Login");
        System.out.println("2 = Kunde registrieren");
        System.out.println("0 = Beenden");
        System.out.print("> ");
    }
    /*private void gibMenu() {
        System.out.println("Befehle:");
        System.out.println("  Login (M oder K):      l");
        System.out.println("  Neuer Artikel:         n");
        System.out.println("  Artikel anzeigen:      a");
        System.out.println("  Artikel in Warenkorb:  h");
        System.out.println("  Warenkorb anzeigen:    w");
        System.out.println("  Kaufen:                k");
        System.out.println("  Ereignisse anzeigen:   e");
        System.out.println("  Menge ändern:          m");
        System.out.println("  -------------------------");
        System.out.println("  Beenden:               q");
        System.out.print("> ");
        System.out.flush();
    }*/
    private String liesEingabe() throws IOException {
        return in.readLine();
    }
    private void artikelAnzeigen() {
        for (Artikel a : shop.getArtikelList()) {
            System.out.println(a.getArtikelId() + ": " + a.getName() + " Bestand: "
                    + a.getBestand() + " Preis: " + a.getPreis());
        }
    }

    private void artikelInWarenkorbLegen() throws IOException {
        artikelAnzeigen();
        System.out.print("Artikelnummer > ");
        int artikelId = Integer.parseInt(liesEingabe());
        System.out.print("Menge > ");
        int menge = Integer.parseInt(liesEingabe());
        // Artikel suchen
        Artikel gefunden = null;
        for (Artikel artikel : shop.getArtikelList()) {
            if (artikel.getArtikelId() == artikelId) {
                gefunden = artikel;
            }
        }
        if (gefunden == null) {
            System.out.println("Artikel nicht gefunden.");
        } else {
            aktuellerKunde.getWarenkorb().addArtikel(gefunden, menge);
            System.out.println(gefunden.getName() + " wurde in den Warenkorb gelegt.");
        }
    }

    private void warenkorbAnzeigen() {
        if (aktuellerKunde.getWarenkorb().getEintraege().isEmpty()) {
            System.out.println("Warenkorb ist leer.");
        }
        else {
            for (WarenkorbEintrag e : aktuellerKunde.getWarenkorb().getEintraege()) {
                System.out.println(
                        e.getArtikel().getName() + " x" + e.getMenge()
                );
            }
        }
    }

    private void mengeAendern() throws IOException {
        /*Menge andern*/
        System.out.print("Artikelnummer > ");
        int artikelNr = Integer.parseInt(liesEingabe());
        System.out.print("Neue Menge > ");
        int neueMenge = Integer.parseInt(liesEingabe());
        aktuellerKunde.getWarenkorb().mengeAendern(artikelNr, neueMenge);
        System.out.println("Menge wurde geändert.");
    }

    private void kaufen() {

        if (aktuellerKunde.getWarenkorb().getEintraege().isEmpty()) {
            System.out.println("Warenkorb ist leer.");
        }
        else {
            try {
                Rechnung r = shop.kaufen(aktuellerKunde);

                System.out.println("Rechnung:");
                System.out.println("Kunde: " + r.getKunde().getName());
                System.out.println("Datum: " + r.getDatum());

                for (WarenkorbEintrag e : r.getWarenkorbList()) {
                    System.out.println(
                            e.getArtikel().getName()
                                    + " x"
                                    + e.getMenge()
                    );
                }
                System.out.println("Gesamtpreis: " + r.getGesamtpreis());

            } catch (NichtGenugBestandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void neuerArtikel() throws IOException {
        if (!(eingeloggterBenutzer instanceof Mitarbeiter)) {
            System.out.println("Nur Mitarbeiter dürfen Artikel anlegen.");
        } else {

            System.out.print("Artikelnummer > ");
            int id = Integer.parseInt(liesEingabe());

            System.out.print("Name > ");
            String name = liesEingabe();

            System.out.print("Bestand > ");
            int bestand = Integer.parseInt(liesEingabe());

            System.out.print("Preis > ");
            double preis = Double.parseDouble(liesEingabe());

            Artikel artikel = new Artikel(id, name, bestand, preis);
            try {
                shop.addArtikel(artikel);
                System.out.println("Artikel wurde angelegt.");
            } catch (ArtikelExistiertBereitsException e) {
                System.out.println(e.getMessage());
            }
        }

    }
    private void artikelLoeschen() throws IOException {
        artikelAnzeigen();

        System.out.print("Artikelnummer > ");
        int artikelId = Integer.parseInt(liesEingabe());

        try {
            shop.artikelLoeschen(artikelId);
            System.out.println("Artikel wurde gelöscht.");
        } catch (ArtikelExistiertNichtException e) {
            System.out.println(e.getMessage());
        }
    }
    private void bestandAendern() throws IOException {
        System.out.print("Artikelnummer > ");
        int artikelId = Integer.parseInt(liesEingabe());

        System.out.print("Neuer Bestand > ");
        int neuerBestand = Integer.parseInt(liesEingabe());

        boolean gefunden = false;
        for (Artikel artikel : shop.getArtikelList()) {

            if (artikel.getArtikelId() == artikelId) {
                gefunden = true;
                artikel.setBestand(neuerBestand);
                int tag= LocalDate.now().getDayOfYear();
                LagerEreignis ereignis =
                        new LagerEreignis(tag, artikel, neuerBestand, (Mitarbeiter) eingeloggterBenutzer,"EIN");

                shop.getEreignisse().add(ereignis);

                System.out.println("Bestand wurde geändert.");
            }
        }
        if (!gefunden) {
            System.out.println("Artikel nicht gefunden.");
        }
    }

    private void ereignisseAnzeigen() {
        for (LagerEreignis e : shop.getEreignisse()) {
            System.out.println(
                    e.getDatum() +" "+e.getTyp()+" " +e.getArtikel().getName()+" "+e.getAnzahl()
            );
        }
    }
    private void login() throws IOException, LoginFehlgeschlagenException {
        System.out.print("Benutzerkennung > ");
        String username = liesEingabe();

        System.out.print("Passwort > ");
        String passwort = liesEingabe();
        //Login verifizieren
        eingeloggterBenutzer = shop.login(username, passwort);

        if (eingeloggterBenutzer != null) {
            System.out.println("Login erfolgreich: " + eingeloggterBenutzer.getName());
            if (eingeloggterBenutzer instanceof Kunde) {
                aktuellerKunde = (Kunde) eingeloggterBenutzer;
                kundenMenue();
            } else if (eingeloggterBenutzer instanceof Mitarbeiter) {
                mitarbeiterMenue();
            }
        } else {
            System.out.println("Login fehlgeschlagen.");
        }

    }
    private void kundeRegistrieren() throws IOException {
        System.out.print("Name > ");
        String name = liesEingabe();

        System.out.print("Adresse > ");
        String adresse = liesEingabe();

        System.out.print("Benutzerkennung > ");
        String benutzerkennung = liesEingabe();

        System.out.print("Passwort > ");
        String passwort = liesEingabe();

        Kunde kunde = new Kunde(shop.getNeuBenutzerId(), name, adresse, benutzerkennung, passwort);
        shop.kundeRegistrieren(kunde);
        System.out.println("Kunde registriert.");
        eingeloggterBenutzer = kunde;
        aktuellerKunde = kunde;
        kundenMenue();
    }
    public void run() {
        String input = "";

        do {
            startMenue();

            try {

                input = liesEingabe();

                switch (input) {
                    case "1" -> login();
                    case "2" -> kundeRegistrieren();
                    case "0" -> System.out.println("Programm beendet.");
                }

            }
            catch (LoginFehlgeschlagenException e) {
                System.out.println(e.getMessage());
            }
            catch (IOException e) {
                System.out.println("Fehler bei der Eingabe.");
            }

        } while (!input.equals("0"));
    }

    public static void main(String[] args) {
        ShopClientUI cui = new ShopClientUI();
        cui.run();
    }
}

