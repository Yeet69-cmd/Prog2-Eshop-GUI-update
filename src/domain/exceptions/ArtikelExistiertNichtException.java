package domain.exceptions;

public class ArtikelExistiertNichtException extends Exception {
    public ArtikelExistiertNichtException(int artikelId) {
        super("Artikel mit ID " + artikelId + " wurde nicht gefunden.");
    }
}