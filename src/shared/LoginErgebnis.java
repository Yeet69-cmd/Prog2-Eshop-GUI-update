package shared;

public class LoginErgebnis {

    private final String benutzerkennung;
    private final boolean erfolgreich;
    private final String rolle;
    private final String name;
    private final String fehlermeldung;

    public LoginErgebnis(
            boolean erfolgreich,
            String rolle,
            String name,
            String benutzerkennung,
            String fehlermeldung
    ) {
        this.erfolgreich = erfolgreich;
        this.rolle = rolle;
        this.name = name;
        this.benutzerkennung = benutzerkennung;
        this.fehlermeldung = fehlermeldung;
    }

    public boolean isErfolgreich() {
        return erfolgreich;
    }

    public String getRolle() {
        return rolle;
    }

    public String getName() {
        return name;
    }

    public String getFehlermeldung() {
        return fehlermeldung;
    }
    public String getBenutzerkennung() {
        return benutzerkennung;
    }
}