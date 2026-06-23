package domain;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Warenkorb implements Serializable{
    private List<WarenkorbEintrag> eintraege =new ArrayList<>() ;

    public List<WarenkorbEintrag> getEintraege() {
        return eintraege;
    }

    public void setEintraege(List<WarenkorbEintrag> eintraege) {
        this.eintraege = eintraege;
    }
    public void addArtikel(Artikel artikel, int menge) {
        WarenkorbEintrag eintrag = new WarenkorbEintrag(artikel, menge);
        eintraege.add(eintrag);
    }
    public void mengeAendern(int artikelId, int neueMenge) {
        for (WarenkorbEintrag eintrag : eintraege) {
            if (eintrag.getArtikel().getArtikelId() == artikelId) {
                eintrag.setMenge(neueMenge);
            }
        }
    }
    public void leeren() {
        eintraege.clear();
    }
}
