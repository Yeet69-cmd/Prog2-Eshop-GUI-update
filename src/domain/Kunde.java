package domain;
import java.io.Serializable;

public class Kunde extends Benutzer implements Serializable{

    private Warenkorb warenkorb;
    private String adresse;

    public Kunde(int id, String name,  String adresse, String Benutzerkennung, String Passwort) {
        super(id, name, Benutzerkennung, Passwort);
        this.adresse = adresse;
        this.warenkorb = new Warenkorb();
    }
    public Warenkorb getWarenkorb() {
        return warenkorb;
    }
}