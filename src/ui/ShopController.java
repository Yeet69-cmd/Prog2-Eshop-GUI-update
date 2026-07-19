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
import client.ShopClient;
import shared.LoginErgebnis;

import java.util.List;

public class ShopController {
    private Benutzer eingeloggterBenutzer;
    private String eingeloggteBenutzerkennung;
    private Kunde aktuellerKunde;
    private ShopService shopService = new ShopService();
    private final ShopClient shopClient = new ShopClient();

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

        //clear old graph data before drawing new one
        bestandChart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(artikel.getName());
        //get the stock values (one per day)
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
        //xaxis fix so it shows per day not half or 2.5
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

            LoginErgebnis ergebnis =
                    shopClient.login(benutzername, passwort);

            if (!ergebnis.isErfolgreich()) {
                loginStatusLabel.setText(
                        ergebnis.getFehlermeldung()
                );
                return;
            }
            eingeloggteBenutzerkennung = ergebnis.getBenutzerkennung();
            if ("KUNDE".equals(ergebnis.getRolle())) {

                artikelTab.setDisable(true);
                warenkorbTab.setDisable(false);

                lagerTab.setDisable(true);
                ereignisseTab.setDisable(true);
                mitarbeiterRegistrierenTab.setDisable(true);
                bestandhistorieTab.setDisable(true);

                loginStatusLabel.setText(
                        "Kunde eingeloggt: "
                                + ergebnis.getName()
                );

            } else if ("MITARBEITER".equals(ergebnis.getRolle())) {

                artikelTab.setDisable(false);
                lagerTab.setDisable(false);
                ereignisseTab.setDisable(false);
                mitarbeiterRegistrierenTab.setDisable(false);

                warenkorbTab.setDisable(false);
                bestandhistorieTab.setDisable(false);

                loginStatusLabel.setText("Mitarbeiter eingeloggt: " + ergebnis.getName());
            }

        } catch (Exception e) {
            loginStatusLabel.setText("Server nicht erreichbar: " + e.getMessage()
            );
        }
    }
    @FXML
    public void artikelAnzeigen() {
        artikelTextArea.clear();
        try {
            List<String> artikelListe = shopClient.getArtikel();
            for (String artikel : artikelListe) {
                artikelTextArea.appendText(artikel + "\n");
            }
        } catch (Exception e) {
            artikelTextArea.setText(
                    "Server nicht erreichbar: " + e.getMessage()
            );
        }
    }
    @FXML
    public void einlagern() {

        try {
            int artikelId = Integer.parseInt(lagerArtikelnummerField.getText());
            int menge = Integer.parseInt(lagerMengeField.getText());
            String meldung = shopClient.einlagern(artikelId, menge);
            lagerStatusLabel.setText(meldung);
            lagerArtikelnummerField.clear();
            lagerMengeField.clear();
            artikelAnzeigen();

        } catch (NumberFormatException e) {
            lagerStatusLabel.setText("Fehler: Artikel-ID und Menge müssen Zahlen sein");

        } catch (Exception e) {
            lagerStatusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML
    public void auslagern() {

        try {
            int artikelId = Integer.parseInt(lagerArtikelnummerField.getText());
            int menge = Integer.parseInt(lagerMengeField.getText());
            String meldung = shopClient.auslagern(artikelId, menge);
            lagerStatusLabel.setText(meldung);
            lagerArtikelnummerField.clear();
            lagerMengeField.clear();

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            lagerStatusLabel.setText(
                    "Fehler: Artikel-ID und Menge müssen Zahlen sein"
            );

        } catch (Exception e) {
            lagerStatusLabel.setText(
                    "Fehler: " + e.getMessage()
            );
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

            String name = artikelNameField.getText().trim();

            int bestand = Integer.parseInt(artikelBestandField.getText());

            double preis = Double.parseDouble(
                    artikelPreisField.getText().replace(",", ".")
            );

            boolean massengut = massengutCheckBox.isSelected();

            int packungsgroesse = 0;
            if (massengut) {
                packungsgroesse = Integer.parseInt(
                        packungsgroesseField.getText()
                );
            }
            String meldung = shopClient.artikelAnlegen(id, name, bestand, preis, massengut, packungsgroesse);

            artikelTextArea.setText(meldung);
            artikelIdField.clear();
            artikelNameField.clear();
            artikelBestandField.clear();
            artikelPreisField.clear();
            packungsgroesseField.clear();
            massengutCheckBox.setSelected(false);

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            artikelTextArea.setText(
                    "ID, Bestand, Preis und Packungsgröße müssen gültige Zahlen sein."
            );

        } catch (Exception e) {
            artikelTextArea.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    @FXML
    public void artikelLoeschen() {

        try {
            int artikelId = Integer.parseInt(
                    artikelIdField.getText()
            );

            String meldung =
                    shopClient.artikelLoeschen(artikelId);

            artikelTextArea.setText(meldung);
            artikelIdField.clear();

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            artikelTextArea.setText(
                    "Die Artikel-ID muss eine Zahl sein."
            );

        } catch (Exception e) {
            artikelTextArea.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    @FXML
    public void kundeRegistrieren() {

        String name = kundeNameField.getText().trim();
        String adresse = kundeAdresseField.getText().trim();
        String benutzerkennung =
                kundeBenutzerkennungField.getText().trim();
        String passwort = kundePasswortField.getText();

        if (name.isEmpty() || adresse.isEmpty() || benutzerkennung.isEmpty() || passwort.isEmpty())
        {
            kundeStatusLabel.setText("Bitte alle Felder ausfüllen.");
            return;
        }

        try {
            String meldung = shopClient.kundeRegistrieren(
                    name,
                    adresse,
                    benutzerkennung,
                    passwort
            );

            kundeStatusLabel.setText(meldung);

            kundeNameField.clear();
            kundeAdresseField.clear();
            kundeBenutzerkennungField.clear();
            kundePasswortField.clear();

        } catch (Exception e) {
            kundeStatusLabel.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    @FXML
    public void mitarbeiterRegistrieren() {

        String name = mitarbeiterNameField.getText().trim();
        String benutzerkennung = mitarbeiterBenutzerkennungField.getText().trim();
        String passwort = mitarbeiterPasswortField.getText();

        if (name.isEmpty() || benutzerkennung.isEmpty() || passwort.isEmpty())
        {
            mitarbeiterStatusLabel.setText(
                    "Bitte alle Felder ausfüllen."
            );
            return;
        }

        try {
            String meldung =
                    shopClient.mitarbeiterRegistrieren(
                            name,
                            benutzerkennung,
                            passwort
                    );

            mitarbeiterStatusLabel.setText(meldung);

            mitarbeiterNameField.clear();
            mitarbeiterBenutzerkennungField.clear();
            mitarbeiterPasswortField.clear();

        } catch (Exception e) {
            mitarbeiterStatusLabel.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    // WarenKorb
    @FXML
    public void warenkorbArtikelAnzeigen() {

        warenkorbArtikelTextArea.clear();

        warenkorbArtikelComboBox.getItems().clear();

        for (Artikel artikel : shopService.getArtikelList()) {

            warenkorbArtikelTextArea.appendText(artikel.toString() + "\n");

            warenkorbArtikelComboBox.getItems().add(artikel);
        }
    }
    @FXML
    public void artikelInWarenkorb() {

        try {
            if (eingeloggteBenutzerkennung == null) {
                warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
                return;
            }
            Artikel artikel = warenkorbArtikelComboBox.getValue();
            if (artikel == null) {
                warenkorbTextArea.setText("Bitte einen Artikel auswählen.");
                return;
            }
            int menge = Integer.parseInt(warenkorbMengeField.getText());
            String meldung = shopClient.artikelInWarenkorb(eingeloggteBenutzerkennung, artikel.getArtikelId(), menge);
            warenkorbTextArea.setText(meldung);
        } catch (NumberFormatException e) {
            warenkorbTextArea.setText(
                    "Bitte eine gültige Menge eingeben."
            );
        } catch (Exception e) {

            warenkorbTextArea.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    @FXML
    public void warenkorbAnzeigen() {

        if (eingeloggteBenutzerkennung == null) {
            warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
            return;
        }

        try {
            List<String> eintraege =
                    shopClient.warenkorbAnzeigen(eingeloggteBenutzerkennung);

            warenkorbTextArea.clear();
            if (eintraege.isEmpty()) {
                warenkorbTextArea.setText("Warenkorb ist leer.");
                return;
            }
            for (String eintrag : eintraege) {
                warenkorbTextArea.appendText(eintrag + "\n");
            }

        } catch (Exception e) {
            warenkorbTextArea.setText("Fehler: " + e.getMessage());
        }
    }
    @FXML
    public void kaufen() {

        if (eingeloggteBenutzerkennung == null) {warenkorbTextArea.setText("Bitte zuerst als Kunde einloggen.");
            return;
        }

        try {
            List<String> rechnung = shopClient.kaufen(eingeloggteBenutzerkennung);

            warenkorbTextArea.clear();
            warenkorbTextArea.appendText("Rechnung:\n");

            for (String zeile : rechnung) {
                warenkorbTextArea.appendText(zeile + "\n");
            }
            warenkorbArtikelTextArea.clear();
            warenkorbMengeField.clear();
            //Reloads the changed stock from the server
            artikelAnzeigen();
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
        eingeloggteBenutzerkennung = null;
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