package domain;
import java.io.Serializable;

public class Mitarbeiter extends Benutzer implements Serializable{

    public Mitarbeiter(int id, String name, String Benutzerkennung, String Passwort) {
        super(id, name, Benutzerkennung, Passwort);
    }

}
