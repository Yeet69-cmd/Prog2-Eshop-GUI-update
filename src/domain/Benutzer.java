package domain;
import java.io.Serializable;

public abstract class Benutzer implements Serializable{
    protected int id;
    protected String name;
    protected String Benutzerkennung;
    protected String Passwort;

    public Benutzer(int id, String name, String Benutzerkennung, String Passwort) {
        this.id = id;
        this.name = name;
        this.Benutzerkennung = Benutzerkennung;
        this.Passwort = Passwort;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBenutzerkennung() {
        return Benutzerkennung;
    }
    public String getPasswort() {
        return Passwort;
    }

}
