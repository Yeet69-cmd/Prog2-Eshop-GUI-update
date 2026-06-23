package domain.exceptions;

public class ArtikelExistiertBereitsException extends Exception{
    public ArtikelExistiertBereitsException() {
        super("Artikel existiert bereits");
    }
}
