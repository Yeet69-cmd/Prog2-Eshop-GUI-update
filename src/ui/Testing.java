// Wird aktuell nicht verwendet
/*
package ui;


import domain.*;
import logic.MitarbeiterVerwaltung;
import logic.ShopService;

public class Testing {
    public static void main(String[] args) {
        ShopService shop = new ShopService();
        Artikel a1 = new Artikel(1, "Cola", 10, 2.5);
        Artikel a2 = new Artikel(2, "Chips", 5, 1.5);
        //Artikel addieren//
        shop.addArtikel(a1);
        shop.addArtikel(a2);
        //Artikel Zeigen//
        for (Artikel a : shop.getArtikelList()) {
            System.out.println(a.getName() + " Bestand: " + a.getBestand());
        }

        Mitarbeiter m1 = new Mitarbeiter(1, "Name","Yooo","Azert123");

        MitarbeiterVerwaltung x = new MitarbeiterVerwaltung();
        //Mitarbeiter addieren//
        x.mitarbeiterHinzufugen(m1);
        //Kunde addieren//
        Kunde k1 = new Kunde(1,"Moe","Bremehaven","Moe1","Moeeee66");
        //Warenkorb addieren//
        k1.getWarenkorb().addArtikel(a1, 1);
        k1.getWarenkorb().addArtikel(a2, 2);
        //Warenkorb zeigen//
        for (WarenkorbEintrag e : k1.getWarenkorb().getEintraege()) {
            System.out.println(
                    k1.getName()+" hat "+ e.getArtikel().getName() + " x" + e.getMenge()
            );
        }

        System.out.println(k1.getWarenkorb().getEintraege().size());
        //Kaufen test//
        Rechnung r1 = shop.kaufen(k1);
        System.out.println("Nach dem Kauf:");
        for (Artikel a : shop.getArtikelList()) {
            System.out.println(a.getName() + " Bestand: " + a.getBestand());
        }

        System.out.println("Warenkorb Größe:" + k1.getWarenkorb().getEintraege().size());

        System.out.println("Rechnung");
        System.out.println("Kunde: " + r1.getKunde().getName());
        System.out.println("Datum: " + r1.getDatum());
        for (WarenkorbEintrag e : r1.getWarenkorbList()) {
            System.out.println(
                    e.getArtikel().getName() +
                            " x" + e.getMenge() + " Preis: " + e.getArtikel().getPreis()
            );
        }

        System.out.println("Gesamtpreis: " + r1.getGesamtpreis());
        shop.kundeRegistrieren(k1);
        Benutzer b = shop.login("Moe1", "Moeeee66");

        if (b != null) {
            System.out.println("Login erfolgreich");
        }
        else {
            System.out.println("Login fehlgeschlagen");
        }
        System.out.println("Lagerereignisse:");
        for (LagerEreignis e : shop.getEreignisse()) {
            System.out.println(
                    e.getDatum() + " " +
                            e.getTyp()+" " +e.getArtikel().getName() +" "+e.getAnzahl()+" durch "+e.getBenutzer().getName()
            );
        }

    }



}
*/
