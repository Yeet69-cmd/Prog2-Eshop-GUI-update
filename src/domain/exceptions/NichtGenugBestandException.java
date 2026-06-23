package domain.exceptions;

public class NichtGenugBestandException extends Exception {
    public NichtGenugBestandException(String artikelName, int gewuenschteMenge, int vorhandenerBestand) {
        super("Nicht genug Bestand für '" + artikelName + "'. Gewünscht: "
                + gewuenschteMenge + ", vorhanden: " + vorhandenerBestand + ".");
    }
}