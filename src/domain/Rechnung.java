package domain;
import java.io.Serializable;
import java.util.List;

public class Rechnung implements Serializable{
    private Kunde kunde;
    private int datum;
    private List<WarenkorbEintrag> warenkorbList;
    private double gesamtpreis;
    /*getters*/
    public Kunde getKunde() {
        return kunde;
    }
    public int getDatum() {
        return datum;
    }
    public List<WarenkorbEintrag> getWarenkorbList() {
        return warenkorbList;
    }
    public double getGesamtpreis() {
        return gesamtpreis;
    }
    public Rechnung(Kunde kunde, int datum, double gesamtpreis, List<WarenkorbEintrag> warenkorbList) {
        this.kunde = kunde;
        this.datum = datum;
        this.gesamtpreis = gesamtpreis;
        this.warenkorbList = warenkorbList;
    }
}
