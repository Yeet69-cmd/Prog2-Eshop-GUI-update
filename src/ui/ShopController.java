package ui;

import client.ShopClient;
import domain.Artikel;
import domain.Benutzer;
import domain.Kunde;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import logic.ShopService;
import shared.LoginErgebnis;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class ShopController {
    private Benutzer eingeloggterBenutzer;
    private String eingeloggteBenutzerkennung;
    private Kunde aktuellerKunde;
    private ShopService shopService = new ShopService();
    private final ShopClient shopClient = new ShopClient();

    /* LOCAL FILES
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
    */

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

        if (artikel == null) {
            bestandChart.setTitle("Bitte zuerst einen Artikel auswählen");
            return;
        }

        bestandChart.getData().clear();

        try {
            List<Integer> historie = shopClient.getBestandsHistorie(artikel.getArtikelId());

            XYChart.Series<Number, Number> series =
                    new XYChart.Series<>();

            series.setName(artikel.getName());

            for (int i = 0; i < historie.size(); i++) {
                series.getData().add(
                        new XYChart.Data<>(
                                i + 1,
                                historie.get(i)
                        )
                );
            }

            bestandChart.getData().add(series);
            bestandChart.setTitle("Bestandsentwicklung der letzten 30 Tage");

        } catch (IOException e) {
            bestandChart.setTitle("Historie konnte nicht geladen werden: " + e.getMessage());
        }
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
            benutzername = benutzername.trim();

            if (benutzername.isEmpty()) {
                loginStatusLabel.setText("Bitte Benutzername eingeben.");
                return;
            }

            if (passwort.isEmpty()) {
                loginStatusLabel.setText("Bitte Passwort eingeben.");
                return;
            }
            LoginErgebnis ergebnis = shopClient.login(benutzername, passwort);

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
            List<Artikel> artikelListe = shopClient.getArtikel();
            if (artikelListe.isEmpty()) {
                artikelTextArea.setText("Keine Artikel vorhanden.");
                return;
            }

            for (Artikel artikel : artikelListe) {
                artikelTextArea.appendText(artikel + "\n");
            }

        } catch (IOException e) {
            artikelTextArea.setText("Artikel konnten nicht geladen werden: " + e.getMessage());
        }
    }
    @FXML
    public void einlagern() {

        if (eingeloggteBenutzerkennung == null) {
            lagerStatusLabel.setText(
                    "Bitte zuerst als Mitarbeiter einloggen."
            );
            return;
        }

        try {
            int artikelId = Integer.parseInt(
                    lagerArtikelnummerField.getText().trim()
            );

            int menge = Integer.parseInt(
                    lagerMengeField.getText().trim()
            );

            if (artikelId <= 0) {
                lagerStatusLabel.setText(
                        "Die Artikel-ID muss größer als 0 sein."
                );
                return;
            }

            if (menge <= 0) {
                lagerStatusLabel.setText(
                        "Die Menge muss größer als 0 sein."
                );
                return;
            }

            if (menge > 100) {
                lagerStatusLabel.setText("Die Menge ist zu groß.");
                return;
            }

            String meldung = shopClient.einlagern(
                    eingeloggteBenutzerkennung,
                    artikelId,
                    menge
            );
            lagerStatusLabel.setText(meldung);
            lagerArtikelnummerField.clear();
            lagerMengeField.clear();

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            lagerStatusLabel.setText("Artikel-ID und Menge müssen ganze Zahlen sein.");

        } catch (Exception e) {
            lagerStatusLabel.setText("Fehler: " + e.getMessage());
        }
    }

    @FXML
    public void auslagern() {

        if (eingeloggteBenutzerkennung == null) {
            lagerStatusLabel.setText("Bitte zuerst als Mitarbeiter einloggen.");
            return;
        }

        try {
            int artikelId = Integer.parseInt(lagerArtikelnummerField.getText().trim());

            int menge = Integer.parseInt(lagerMengeField.getText().trim());

            if (artikelId <= 0) {
                lagerStatusLabel.setText("Die Artikel-ID muss größer als 0 sein.");
                return;
            }

            if (menge <= 0) {
                lagerStatusLabel.setText("Die Menge muss größer als 0 sein.");
                return;
            }

            String meldung = shopClient.auslagern(
                    eingeloggteBenutzerkennung,
                    artikelId,
                    menge
            );

            lagerStatusLabel.setText(meldung);
            lagerArtikelnummerField.clear();
            lagerMengeField.clear();

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            lagerStatusLabel.setText("Artikel-ID und Menge müssen ganze Zahlen sein.");

        } catch (Exception e) {
            lagerStatusLabel.setText(
                    "Fehler: " + e.getMessage()
            );
        }
    }
    @FXML
    public void ereignisseAnzeigen() {

        ereignisTextArea.clear();

        try {
            List<String> ereignisse =
                    shopClient.getEreignisse();

            if (ereignisse.isEmpty()) {
                ereignisTextArea.setText(
                        "Keine Lagerereignisse vorhanden."
                );
                return;
            }

            for (String ereignis : ereignisse) {
                ereignisTextArea.appendText(
                        ereignis + "\n"
                );
            }

        } catch (IOException e) {
            ereignisTextArea.setText(
                    "Ereignisse konnten nicht geladen werden: "
                            + e.getMessage()
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
            if (id <= 0) {
                artikelTextArea.setText(
                        "Die Artikel-ID muss größer als 0 sein."
                );
                return;
            }

            if (name.isBlank()) {
                artikelTextArea.setText("Der Artikelname darf nicht leer sein.");
                return;
            }

            if (name.length() > 50) {
                artikelTextArea.setText("Der Artikelname darf höchstens 50 Zeichen haben.");
                return;
            }

            if (bestand < 0) {
                artikelTextArea.setText("Der Bestand darf nicht negativ sein.");
                return;
            }

            if (bestand > 100) {
                artikelTextArea.setText("Der Bestand ist zu groß.");
                return;
            }

            if (preis <= 0) {
                artikelTextArea.setText("Der Preis muss größer als 0 sein.");
                return;
            }

            if (massengut && packungsgroesse <= 0) {
                artikelTextArea.setText("Die Packungsgröße muss größer als 0 sein.");
                return;
            }

            if (massengut && bestand % packungsgroesse != 0) {
                artikelTextArea.setText("Der Bestand muss ein Vielfaches der Packungsgröße sein.");
                return;
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

        try {
            List<Artikel> artikelListe = shopClient.getArtikel();

            if (artikelListe.isEmpty()) {
                warenkorbArtikelTextArea.setText("Keine Artikel vorhanden.");
                return;
            }

            warenkorbArtikelComboBox.getItems().addAll(artikelListe);
            for (Artikel artikel : artikelListe) {
                warenkorbArtikelTextArea.appendText(artikel + "\n");
            }

        } catch (IOException e) {
            warenkorbArtikelTextArea.setText("Artikel konnten nicht geladen werden: " + e.getMessage()
            );
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
            if (menge <= 0) {
                warenkorbTextArea.setText("Die Menge muss größer als 0 sein.");
                return;
            }

            if (menge > artikel.getBestand()) {
                warenkorbTextArea.setText("Die gewünschte Menge ist größer als der Bestand.");
                return;
            }

            if (menge > 100) {
                warenkorbTextArea.setText("Die Menge ist zu groß.");
                return;
            }
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
            int id = Integer.parseInt(artikelIdField.getText().trim());
            int neuerBestand = Integer.parseInt(artikelBestandField.getText().trim());

            if (id <= 0) {
                artikelTextArea.setText("Die Artikel-ID muss größer als 0 sein.");
                return;
            }

            if (neuerBestand < 0) {
                artikelTextArea.setText("Der Bestand darf nicht negativ sein.");
                return;
            }
            if (neuerBestand > 100) {
                artikelTextArea.setText(
                        "Der Bestand ist zu groß."
                );
                return;
            }

            List<Artikel> artikelListe = shopClient.getArtikel();

            Artikel gesuchterArtikel = null;

            for (Artikel artikel : artikelListe) {
                if (artikel.getArtikelId() == id) {
                    gesuchterArtikel = artikel;
                    break;
                }
            }

            if (gesuchterArtikel == null) {
                artikelTextArea.setText("Artikel nicht gefunden.");
                return;
            }

            int alterBestand = gesuchterArtikel.getBestand();
            int differenz = neuerBestand - alterBestand;

            if (differenz > 0) {
                String meldung = shopClient.einlagern(eingeloggteBenutzerkennung, id, differenz);
                artikelTextArea.setText(meldung);

            } else if (differenz < 0) {
                String meldung = shopClient.auslagern(eingeloggteBenutzerkennung, id, -differenz);
                artikelTextArea.setText(meldung);

            } else {
                artikelTextArea.setText("Der Bestand wurde nicht verändert.");
                return;
            }

            artikelAnzeigen();

        } catch (NumberFormatException e) {
            artikelTextArea.setText("Artikel-ID und Bestand müssen ganze Zahlen sein.");

        } catch (Exception e) {
            artikelTextArea.setText("Bestandsänderung fehlgeschlagen: " + e.getMessage());
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

        try {
            graphArtikelComboBox.getItems().addAll(shopClient.getArtikel());

        } catch (IOException e) {
            bestandChart.setTitle("Artikel konnten nicht geladen werden: " + e.getMessage());
        }
    }
    @FXML
    public void sortiereNachId() {

        artikelTextArea.clear();

        try {
            List<Artikel> artikelListe = shopClient.getArtikel();

            artikelListe.sort(Comparator.comparingInt(Artikel::getArtikelId));

            for (Artikel artikel : artikelListe) {
                artikelTextArea.appendText(artikel + "\n");
            }

        } catch (IOException e) {
            artikelTextArea.setText("Sortieren fehlgeschlagen: " + e.getMessage());
        }
    }
    @FXML
    public void sortiereNachName() {

        artikelTextArea.clear();

        try {
            List<Artikel> artikelListe = shopClient.getArtikel();

            artikelListe.sort(
                    Comparator.comparing(Artikel::getName, String.CASE_INSENSITIVE_ORDER)
            );

            for (Artikel artikel : artikelListe) {artikelTextArea.appendText(artikel + "\n");
            }

        } catch (IOException e) {
            artikelTextArea.setText(
                    "Sortieren fehlgeschlagen: "
                            + e.getMessage()
            );
        }
    }
}