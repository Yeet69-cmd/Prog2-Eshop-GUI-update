package logic;

import domain.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.io.IOException;
import domain.exceptions.LoginFehlgeschlagenException;
import domain.exceptions.ArtikelExistiertBereitsException;
import domain.exceptions.LoginFehlgeschlagenException;
import domain.exceptions.NichtGenugBestandException;
import domain.exceptions.ArtikelExistiertNichtException;

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
                throw new ArtikelExistiertBereitsException();
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
                throw new NichtGenugBestandException(artikel.getName());
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
            LagerEreignis ereignis = new LagerEreignis(tag, artikel, menge, kunde, "AUS");
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

        throw new LoginFehlgeschlagenException();
    }
    public void artikelLoeschen(int artikelId)
            throws ArtikelExistiertNichtException {

        for (Artikel artikel : artikelList) {
            if (artikel.getArtikelId() == artikelId) {
                artikelList.remove(artikel);
                return;
            }
        }

        throw new ArtikelExistiertNichtException();
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
    }

}
