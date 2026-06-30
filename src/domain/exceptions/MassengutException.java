package domain.exceptions;

public class MassengutException extends RuntimeException {
    public MassengutException(int packungsgroesse) {

        super("Nur Vielfache von " + packungsgroesse + " erlaubt.");

    }
}
