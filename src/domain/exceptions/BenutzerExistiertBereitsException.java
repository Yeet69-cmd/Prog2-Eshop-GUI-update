package domain.exceptions;

public class BenutzerExistiertBereitsException extends Exception {
    public BenutzerExistiertBereitsException(String benutzerkennung) {
        super("Benutzerkennung '" + benutzerkennung + "' ist bereits vergeben.");
    }
}
