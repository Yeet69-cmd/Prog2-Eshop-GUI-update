package logic;

import domain.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;
import java.io.IOException;

import domain.exceptions.*;


public class ShopService {
    private List<Artikel> artikelList= new ArrayList<>();
    private List<Benutzer> kundenList= new ArrayList<>();
    private List<Mitarbeiter> mitarbeiterList= new ArrayList<>();
    private List<LagerEreignis>  lagerEreignisList= new ArrayList<>();

    private int neuBenutzerId = 2;
    public int getNeuBenutzerId() {
        return neuBenutzerId++;
    }
    public void addArtikel(Artikel artikel) throws ArtikelExistiertBereitsException {
        for (Artikel a : artikelList) {
            if (a.getArtikelId() == artikel.getArtikelId()) {
                throw new ArtikelExistiertBereitsException(artikel.getArtikelId());
            }
        }

        artikelList.add(artikel);
    }
    public List<Artikel> getArtikelList() {
        return artikelList;
    }
    public List<LagerEreignis> getEreignisse() {
        return lagerEreignisList ;
    }
    public Rechnung kaufen(Kunde kunde) throws NichtGenugBestandException {
        // Prüfen, ob genügend Lagerbestand vorhanden ist
        for (WarenkorbEintrag eintrag : kunde.getWarenkorb().getEintraege()) {
            Artikel artikel = eintrag.getArtikel();
            int menge = eintrag.getMenge();

            if (menge > artikel.getBestand()) {
                throw new NichtGenugBestandException(artikel.getName(), menge, artikel.getBestand());
            }

        }
        // jetzt kann man kaufen
        double sum=0;
        LocalDate heute = LocalDate.now();
        int tag = heute.getDayOfYear();
        for(WarenkorbEintrag eintrag : kunde.getWarenkorb().getEintraege()) {
            Artikel artikel = eintrag.getArtikel();
            sum=sum+artikel.getPreis()*eintrag.getMenge();
            int menge = eintrag.getMenge();
            artikel.setBestand(artikel.getBestand() - menge);
            /* Artikel Auslegen*/
            LagerEreignis ereignis = new LagerEreignis(tag, artikel, menge, kunde, "AUS",artikel.getBestand());
            lagerEreignisList.add(ereignis);
        }
        List<WarenkorbEintrag> gekaufteEintraege =
                new ArrayList<>(kunde.getWarenkorb().getEintraege());
        Rechnung rechnung = new Rechnung(kunde, tag, sum, gekaufteEintraege);

        kunde.getWarenkorb().leeren();
        return rechnung;
    }
    public void kundeRegistrieren(Kunde kunde) {
        this.kundenList.add(kunde);
    }
    public void mitarbeiterRegistrieren(Mitarbeiter mitarbeiter) {
        this.mitarbeiterList.add(mitarbeiter);
    }
    public Benutzer login(String benutzername, String password)
            throws LoginFehlgeschlagenException {

        for (Benutzer benutzer : kundenList) {
            if (benutzer.getBenutzerkennung().equals(benutzername)
                    && benutzer.getPasswort().equals(password)) {
                return benutzer;
            }
        }

        for (Mitarbeiter mitarbeiter : mitarbeiterList) {
            if (mitarbeiter.getBenutzerkennung().equals(benutzername)
                    && mitarbeiter.getPasswort().equals(password)) {
                return mitarbeiter;
            }
        }

        throw new LoginFehlgeschlagenException(benutzername);
    }
    public void artikelLoeschen(int artikelId)
            throws ArtikelExistiertNichtException {

        for (Artikel artikel : artikelList) {
            if (artikel.getArtikelId() == artikelId) {
                artikelList.remove(artikel);
                return;
            }
        }

        throw new ArtikelExistiertNichtException(artikelId);
    }
    public boolean hatArtikel() {
        return !artikelList.isEmpty();
    }
    // Datei Speichern
    public void speichern(){
        try {
            DateiService.speichern(artikelList, "artikel.dat");
            DateiService.speichern(kundenList, "kunden.dat");
            DateiService.speichern(mitarbeiterList, "mitarbeiter.dat");
            DateiService.speichern(lagerEreignisList, "ereignisse.dat");


        } catch (IOException e) {

        }
    }
    //Datei Laden
    public void laden(){
        try {
            artikelList =(List<Artikel>) DateiService.laden("artikel.dat");
            kundenList =(List<Benutzer>) DateiService.laden("kunden.dat");
            mitarbeiterList =(List<Mitarbeiter>) DateiService.laden("mitarbeiter.dat");
            lagerEreignisList =(List<LagerEreignis>) DateiService.laden("ereignisse.dat");



        } catch (IOException | ClassNotFoundException e) {

        }
        System.out.println("Ordner: " + System.getProperty("user.dir"));
        System.out.println("artikel.dat existiert: " + new java.io.File("artikel.dat").exists());
        System.out.println("artikel.dat pfad: " + new java.io.File("artikel.dat").getAbsolutePath());
    }
    public void einlagern(int artikelId, int menge)
            throws ArtikelExistiertNichtException,MassengutException {

        for (Artikel artikel : artikelList) {
            if (artikel.getArtikelId() == artikelId) {
                if (artikel instanceof Massengutartikel) {
                    Massengutartikel m = (Massengutartikel) artikel;
                    if (menge % m.getPackungsgroesse() != 0) {
                        throw new MassengutException(m.getPackungsgroesse());
                    }
                }
                artikel.setBestand(artikel.getBestand() + menge);
                int tag = LocalDate.now().getDayOfYear();
                lagerEreignisList.add(
                        new LagerEreignis(tag, artikel, menge, null, "EIN", artikel.getBestand())
                );
                speichern();
                return;
            }
        }
        throw new ArtikelExistiertNichtException(artikelId);
    }
    public void auslagern(int artikelId, int menge)
            throws ArtikelExistiertNichtException, NichtGenugBestandException, MassengutException {
        for (Artikel artikel : artikelList) {
            if (artikel.getArtikelId() == artikelId) {

                if (menge > artikel.getBestand()) {
                    throw new NichtGenugBestandException(
                            artikel.getName(),
                            menge,
                            artikel.getBestand()
                    );
                }
                if (artikel instanceof Massengutartikel) {

                    Massengutartikel m = (Massengutartikel) artikel;

                    if (menge % m.getPackungsgroesse() != 0) {
                        throw new MassengutException(
                                m.getPackungsgroesse()
                        );
                    }

                }
                artikel.setBestand(artikel.getBestand() - menge);
                int tag = LocalDate.now().getDayOfYear();
                lagerEreignisList.add(
                        new LagerEreignis(tag, artikel, menge, null, "AUS", artikel.getBestand()
                        )
                );
                speichern();
                return;
            }
        }
        throw new ArtikelExistiertNichtException(artikelId);
    }
    public void sortiereNachId() {
        artikelList.sort(
                Comparator.comparingInt(Artikel::getArtikelId)
        );
    }

    public void sortiereNachName() {
        artikelList.sort(
                Comparator.comparing(Artikel::getName)
        );
    }
    public List<Integer> getBestandsHistorie(Artikel artikel) {
        int heute = LocalDate.now().getDayOfYear();
        List<Integer> result = new ArrayList<>();
        for (int tag = heute - 29; tag <= heute; tag++) {
            int bestandAnDiesemTag = 0;
            for (LagerEreignis e : lagerEreignisList) {
                if (e.getArtikel().getArtikelId() == artikel.getArtikelId()
                        && e.getDatum() <= tag) {
                    bestandAnDiesemTag = e.getBestandNachher();
                }
            }
            result.add(bestandAnDiesemTag);
        }
        return result;
    }
}
