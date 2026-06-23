package domain.exceptions;

public class NichtGenugBestandException extends Exception {
    public NichtGenugBestandException(String artikelName) {
        super("Nicht genug Bestand für: " + artikelName);
    }
}
