package domain.exceptions;

public class ArtikelExistiertNichtException extends Exception {

    public ArtikelExistiertNichtException() {
        super("Artikel existiert nicht.");
    }
}