package domain;
import java.io.Serializable;

public class Artikel implements Serializable{
    private final int artikelId;
    private final String artikelName;
    private int bestand;
    private double preis;
    // Getters und Setters
    public String getName(){
        return artikelName;
    }
    public int getArtikelId() {
        return artikelId;
    }
    public int getBestand(){
        return bestand;
    }
    public void setBestand(int bestand){
        this.bestand=bestand;
    }
    public double getPreis(){
        return preis;
    }
    public void setPreis(double preis) {
        this.preis = preis;
    }
    public Artikel(int artikelId, String artikelName, int bestand, double preis) {
        this.artikelId = artikelId;
        this.artikelName = artikelName;
        this.preis = preis;
        this.bestand = bestand;
    }
}
