package domain.exceptions;

public class ArtikelExistiertBereitsException extends Exception {
    public ArtikelExistiertBereitsException(int artikelId) {
        super("Artikel mit ID " + artikelId + " existiert bereits.");
    }
}