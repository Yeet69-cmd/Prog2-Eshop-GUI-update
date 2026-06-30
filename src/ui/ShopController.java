package ui;

import domain.Artikel;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import logic.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logic.ShopService;
import domain.*;

import java.util.List;

public class ShopController {
    private Benutzer eingeloggterBenutzer;
    private Kunde aktuellerKunde;
    private ShopService shopService = new ShopService();

    public ShopController() {
        shopService.laden();

        if (!shopService.hatArtikel()) {
            try {
                shopService.addArtikel(new Artikel(1, "Cola", 10, 2.5));
                shopService.addArtikel(new Artikel(2, "Chips", 5, 1.5));
                shopService.speichern();
                aktuellerKunde = new Kunde(1, "selim", "Bremerhaven", "selim1", "1234");
                shopService.kundeRegistrieren(aktuellerKunde);
                Mitarbeiter admin = new Mitarbeiter(1, "Admin", "admin", "1234");
                shopService.mitarbeiterRegistrieren(admin);

                System.out.println("Testdaten erstellt und gespeichert.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private CheckBox massengutCheckBox;

    @FXML
    private TextField packungsgroesseField;
    @FXML
    private TabPane shopTabPane;
    @FXML
    private TextField loginNameField;
    @FXML
    private PasswordField loginPasswortField;
    @FXML
    private Label loginStatusLabel;
    @FXML
    private TextArea artikelTextArea;
    @FXML
    private TextField lagerArtikelnummerField;
    @FXML
    private TextField lagerMengeField;
    @FXML
    private Label lagerStatusLabel;
    @FXML
    private TextArea ereignisTextArea;
    @FXML
    private TextField artikelIdField;
    @FXML
    private TextField artikelNameField;
    @FXML
    private TextField artikelBestandField;
    @FXML
    private TextField artikelPreisField;

    @FXML private TextField kundeNameField;
    @FXML private TextField kundeAdresseField;
    @FXML private TextField kundeBenutzerkennungField;
    @FXML private PasswordField kundePasswortField;
    @FXML private Label kundeStatusLabel;

    @FXML private TextField mitarbeiterNameField;
    @FXML private TextField mitarbeiterBenutzerkennungField;
    @FXML private PasswordField mitarbeiterPasswortField;
    @FXML private Label mitarbeiterStatusLabel;

    @FXML private Tab loginTab;
    @FXML private Tab kundeRegistrierenTab;
    @FXML private Tab mitarbeiterRegistrierenTab;
    @FXML
    private Tab artikelTab;
    @FXML
    private Tab lagerTab;
    @FXML
    private Tab ereignisseTab;
    @FXML private Tab warenkorbTab;
    @FXML
    private TextArea warenkorbArtikelTextArea;
    @FXML
    private ComboBox<Artikel> warenkorbArtikelComboBox;
    @FXML
    private TextField warenkorbMengeField;
    @FXML
    private TextArea warenkorbTextArea;
    @FXML
    private ComboBox<Artikel> graphArtikelComboBox;

    @FXML
    private LineChart<Number, Number> bestandChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;
    @FXML
    private Tab bestandhistorieTab;

    @FXML
    public void graphAnzeigen() {
        Artikel artikel = graphArtikelComboBox.getValue();
        if (artikel == null) return;

        bestandChart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(artikel.getName());

        List<Integer> historie = shopService.getBestandsHistorie(artikel);
        for (int i = 0; i < historie.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, historie.get(i)));
        }

        bestandChart.getData().add(series);
    }
    @FXML
    public void initialize() {
        artikelTab.setDisable(true);
        lagerTab.setDisable(true);
        ereignisseTab.setDisable(true);
        warenkorbTab.setDisable(true);
        mitarbeiterRegistrierenTab.setDisable(true);
        bestandhistorieTab.setDisable(true);
        //xaxis fix for the full day
        xAxis.setTickUnit(1);
        xAxis.setMinorTickCount(0);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(1);
        xAxis.setUpperBound(30);
    }
    @FXML
    public void login() {
        try {
            String benutzername = loginNameField.getText();
            String passwort = loginPasswortField.getText();

            eingeloggterBenutzer = shopService.login(benutzername, passwort);

            if (eingeloggterBenutzer instanceof Kunde) {
                aktuellerKunde = (Kunde) eingeloggterBenutzer;

                artikelTab.setDisable(true);
                warenkorbTab.setDisable(false);

                lagerTab.setDisable(true);
                ereignisseTab.setDisable(true);
                mitarbeiterRegistrierenTab.setDisable(true);
                bestandhistorieTab.setDisable(false);

                loginStatusLabel.setText("Kunde eingeloggt: " + aktuellerKunde.getName());
            } else if (eingeloggterBenutzer instanceof Mitarbeiter) {
                artikelTab.setDisable(false);
                lagerTab.setDisable(false);
                ereignisseTab.setDisable(false);
                mitarbeiterRegistrierenTab.setDisable(false);

                warenkorbTab.setDisable(true);
                bestandhistorieTab.setDisable(false);

                loginStatusLabel.setText("Mitarbeiter eingeloggt: " + eingeloggterBenutzer.getName());
            }

        } catch (Exception e) {
            loginStatusLabel.setText(e.getMessage());
        }
    }
    @FXML
    public void artikelAnzeigen() {
        artikelTextArea.clear();

        for (Artikel artikel : shopService.getArtikelList()) {
            artikelTextArea.appendText(artikel.toString() + "\n");
        }
    }
    @FXML
    public void einlagern() {
        try {
            int artikelId = Integer.parseInt(lagerArtikelnummerField.getText());
            int menge = Integer.parseInt(lagerMengeField.getText());

            shopService.einlagern(artikelId, menge);

            lagerStatusLabel.setText("Einlagerung erfolgreich");
            artikelAnzeigen();

        } catch (Exception e) {
            lagerStatusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML
    public void auslagern() {
        try {
            int artikelId = Integer.parseInt(lagerArtikelnummerField.getText());
            int menge = Integer.parseInt(lagerMengeField.getText());

            shopService.auslagern(artikelId, menge);

            lagerStatusLabel.setText("Auslagerung erfolgreich");
            artikelAnzeigen();

        } catch (Exception e) {
            lagerStatusLabel.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void ereignisseAnzeigen() {
        ereignisTextArea.clear();

        for (LagerEreignis e : shopService.getEreignisse()) {
            ereignisTextArea.appendText(
                    e.toString() + "\n"
            );
        }
    }
    @FXML
    public void artikelAnlegen() {
        try {
            int id = Integer.parseInt(artikelIdField.getText());
            String name = artikelNameField.getText();
            int bestand = Integer.parseInt(artikelBestandField.getText());
            double preis = Double.parseDouble(artikelPreisField.getText());

            Artikel artikel;
            //Packungsgroesse checkbox
            if (massengutCheckBox.isSelected()) {

                int packungsgroesse = Integer.parseInt(packungsgroesseField.getText());
                artikel = new Massengutartikel(id, name, bestand, preis, packungsgroesse);

            } else {
                artikel = new Artikel(id, name, bestand, preis);
            }
            shopService.addArtikel(artikel);
            shopService.speichern();
            //clear artikel fields
            artikelIdField.clear();
            artikelNameField.clear();
            artikelBestandField.clear();
            artikelPreisField.clear();
            packungsgroesseField.clear();
            massengutCheckBox.setSelected(false);

            artikelAnzeigen();

        } catch (Exception e) {
            artikelTextArea.setText(e.getMessage());
        }
    }
    @FXML
    public void artikelLoeschen() {

        try {
            int artikelId =
                    Integer.parseInt(
                            artikelIdField.getText()
                    );
            shopService.artikelLoeschen(artikelId);
            shopService.speichern();
            artikelAnzeigen();
        } catch (Exception e) {
            artikelTextArea.setText(e.getMessage());
        }
    }
    @FXML
    public void kundeRegistrieren() {
        try {
            Kunde kunde = new Kunde(
                    shopService.getNeuBenutzerId(),
                    kundeNameField.getText(),
                    kundeAdresseField.getText(),
                    kundeBenutzerkennungField.getText(),
                    kundePasswortField.getText()
            );
            shopService.kundeRegistrieren(kunde);
            shopService.speichern();
            kundeStatusLabel.setText("Kunde registriert: " + kunde.getName());

        } catch (Exception e) {
            kundeStatusLabel.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void mitarbeiterRegistrieren() {
        try {
            Mitarbeiter mitarbeiter = new Mitarbeiter(
                    shopService.getNeuBenutzerId(),
                    mitarbeiterNameField.getText(),
                    mitarbeiterBenutzerkennungField.getText(),
                    mitarbeiterPasswortField.getText()
            );

            shopService.mitarbeiterRegistrieren(mitarbeiter);
            shopService.speichern();

            mitarbeiterStatusLabel.setText("Mitarbeiter registriert.");

        } catch (Exception e) {
            mitarbeiterStatusLabel.setText(e.getMessage());
        }
    }
    // WarenKorb
    @FXML
    public void warenkorbArtikelAnzeigen() {

        warenkorbArtikelTextArea.clear();

        warenkorbArtikelComboBox.getItems().clear();

        for (Artikel artikel : shopService.getArtikelList()) {

            warenkorbArtikelTextArea.appendText(
                    artikel.toString() + "\n"
            );

            warenkorbArtikelComboBox.getItems().add(artikel);
        }
    }
    @FXML
    public void artikelInWarenkorb() {
        try {
            if (aktuellerKunde == null) {
                warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
                return;
            }

            Artikel artikel = warenkorbArtikelComboBox.getValue();

            if (artikel == null) {
                warenkorbTextArea.setText("Bitte einen Artikel auswählen.");
                return;
            }

            int menge = Integer.parseInt(warenkorbMengeField.getText());

            aktuellerKunde.getWarenkorb().addArtikel(artikel, menge);

            warenkorbTextArea.setText(
                    artikel.getName() + " x " + menge + " wurde in den Warenkorb gelegt."
            );

        } catch (Exception e) {
            warenkorbTextArea.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void warenkorbAnzeigen() {
        if (aktuellerKunde == null) {
            warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
            return;
        }

        warenkorbTextArea.clear();

        if (aktuellerKunde.getWarenkorb().getEintraege().isEmpty()) {
            warenkorbTextArea.setText("Warenkorb ist leer.");
            return;
        }

        for (WarenkorbEintrag e : aktuellerKunde.getWarenkorb().getEintraege()) {
            warenkorbTextArea.appendText(
                    e.getArtikel().getName()
                            + " x "
                            + e.getMenge()
                            + "\n"
            );
        }
    }
    @FXML
    public void kaufen() {
        try {
            if (aktuellerKunde == null) {
                warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
                return;
            }

            Rechnung r = shopService.kaufen(aktuellerKunde);
            shopService.speichern();

            warenkorbTextArea.clear();
            warenkorbTextArea.appendText("Rechnung:\n");
            warenkorbTextArea.appendText("Kunde: " + r.getKunde().getName() + "\n");
            warenkorbTextArea.appendText("Datum: " + r.getDatum() + "\n\n");

            for (WarenkorbEintrag e : r.getWarenkorbList()) {
                warenkorbTextArea.appendText(
                        e.getArtikel().getName() + " x " + e.getMenge() + "\n"
                );
            }

            warenkorbTextArea.appendText("\nGesamtpreis: " + r.getGesamtpreis() + " €");

            artikelAnzeigen();
            warenkorbArtikelTextArea.clear();

        } catch (Exception e) {
            warenkorbTextArea.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void bestandAndern() {
        try {
            int id = Integer.parseInt(artikelIdField.getText());
            int neuerBestand = Integer.parseInt(artikelBestandField.getText());

            for (Artikel artikel : shopService.getArtikelList()) {
                if (artikel.getArtikelId() == id) {

                    int alterBestand = artikel.getBestand();
                    int differenz = neuerBestand - alterBestand;

                    if (differenz > 0) {
                        shopService.einlagern(id, differenz);
                    } else if (differenz < 0) {
                        shopService.auslagern(id, -differenz);
                    }

                    artikelAnzeigen();
                    return;
                }
            }

            artikelTextArea.setText("Artikel nicht gefunden.");

        } catch (Exception e) {
            artikelTextArea.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void logout() {
        eingeloggterBenutzer = null;
        aktuellerKunde = null;

        loginNameField.clear();
        loginPasswortField.clear();

        loginStatusLabel.setText("Nicht eingeloggt");

        artikelTab.setDisable(true);
        lagerTab.setDisable(true);
        ereignisseTab.setDisable(true);
        warenkorbTab.setDisable(true);
        mitarbeiterRegistrierenTab.setDisable(true);

        kundeRegistrierenTab.setDisable(false);
        loginTab.setDisable(false);
        shopTabPane.getSelectionModel().select(loginTab);
    }
    @FXML
    public void graphArtikelLaden() {
        graphArtikelComboBox.getItems().clear();
        graphArtikelComboBox.getItems().addAll(shopService.getArtikelList());
    }
    @FXML
    public void sortiereNachId() {
        shopService.sortiereNachId();
        artikelAnzeigen();
    }
    @FXML
    public void sortiereNachName() {
        shopService.sortiereNachName();
        artikelAnzeigen();
    }


}