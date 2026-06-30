package domain;
import domain.*;

public class Massengutartikel extends Artikel {

    private int packungsgroesse;

    public Massengutartikel(int artikelId,
                            String artikelName,
                            int bestand,
                            double preis,
                            int packungsgroesse) {

        super(artikelId, artikelName, bestand, preis);
        this.packungsgroesse = packungsgroesse;
    }

    public int getPackungsgroesse() {
        return packungsgroesse;
    }

    public void setPackungsgroesse(int packungsgroesse) {
        this.packungsgroesse = packungsgroesse;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Packungsgröße: "
                + packungsgroesse;
    }
}