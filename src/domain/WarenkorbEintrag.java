package domain;
import java.io.Serializable;

public class WarenkorbEintrag implements Serializable{
    private Artikel artikel;
    private int menge;

    public WarenkorbEintrag(Artikel artikel, int menge) {
        this.artikel = artikel;
        this.menge = menge;
    }

    public Artikel getArtikel() {
        return artikel;
    }
    public int getMenge() {
        return menge;
    }
    public void setMenge(int menge) {
        this.menge = menge;
    }

}
