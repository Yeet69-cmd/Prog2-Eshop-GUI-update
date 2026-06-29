package domain;
import java.io.Serializable;

public class LagerEreignis implements Serializable{
    private int datum;
    private Artikel artikel;
    private int anzahl;
    private Benutzer benutzer;
    private String typ;
    public int getDatum() {
        return datum;
    }
    public Artikel getArtikel() {
        return artikel;
    }
    public int getAnzahl() {
        return anzahl;
    }
    public Benutzer getBenutzer() {
        return benutzer;
    }
    public String getTyp() {
        return typ;
    }
    private int bestandNachher;
    public LagerEreignis(int datum, Artikel artikel, int anzahl, Benutzer benutzer, String typ, int bestandNachher) {
        this.datum = datum;
        this.artikel = artikel;
        this.anzahl = anzahl;
        this.benutzer = benutzer;
        this.typ = typ;
        this.bestandNachher = bestandNachher;
    }
    public int getBestandNachher() {
        return bestandNachher;
    }
    @Override
    public String toString() {
        return datum + " | "
                + typ + " | "
                + artikel.getName()
                + " | Menge: "
                + anzahl;
    }
}
