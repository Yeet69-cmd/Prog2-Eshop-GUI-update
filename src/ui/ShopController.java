package ui;

import domain.Artikel;
import logic.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logic.ShopService;
import domain.*;

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

                System.out.println("Testdaten erstellt und gespeichert.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



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
    public void initialize() {
        artikelTab.setDisable(true);
        lagerTab.setDisable(true);
        ereignisseTab.setDisable(true);
        warenkorbTab.setDisable(true);
        mitarbeiterRegistrierenTab.setDisable(true);
    }
    @FXML
    public void login() {
        try {
            String benutzername = loginNameField.getText();
            String passwort = loginPasswortField.getText();

            eingeloggterBenutzer = shopService.login(benutzername, passwort);

            if (eingeloggterBenutzer instanceof Kunde) {
                aktuellerKunde = (Kunde) eingeloggterBenutzer;

                artikelTab.setDisable(false);
                warenkorbTab.setDisable(false);

                lagerTab.setDisable(true);
                ereignisseTab.setDisable(true);
                mitarbeiterRegistrierenTab.setDisable(true);

                loginStatusLabel.setText("Kunde eingeloggt: " + aktuellerKunde.getName());
            } else if (eingeloggterBenutzer instanceof Mitarbeiter) {
                artikelTab.setDisable(false);
                lagerTab.setDisable(false);
                ereignisseTab.setDisable(false);
                mitarbeiterRegistrierenTab.setDisable(false);

                warenkorbTab.setDisable(true);

                loginStatusLabel.setText("Mitarbeiter eingeloggt: " + eingeloggterBenutzer.getName());
            }

        } catch (Exception e) {
            loginStatusLabel.setText("Login fehlgeschlagen: " + e.getMessage());
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

            Artikel artikel = new Artikel(id, name, bestand, preis);

            shopService.addArtikel(artikel);
            shopService.speichern();

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


}