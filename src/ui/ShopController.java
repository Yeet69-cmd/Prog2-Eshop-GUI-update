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

    @FXML
    public void login(){
        String benutzername = loginNameField.getText();
        String password = loginPasswortField.getText();
        loginStatusLabel.setText("Benutzer: "+benutzername);

    }
    @FXML
    public void artikelAnzeigen() {
        System.out.println("Button geklickt");

        artikelTextArea.clear();

        for (Artikel artikel : shopService.getArtikelList()) {
            artikelTextArea.appendText(artikel.toString() + "\n");
        }
        System.out.println(shopService.getArtikelList().size());

        artikelTextArea.clear();

        for (Artikel artikel : shopService.getArtikelList()) {
            artikelTextArea.appendText(artikel.toString() + "\n");
        }
    }
    @FXML
    public void einlagern() {
        lagerStatusLabel.setText("Einlagern geklickt");
    }

    @FXML
    public void auslagern() {
        lagerStatusLabel.setText("Auslagern geklickt");
    }
    @FXML
    public void ereignisseAnzeigen() {
        ereignisTextArea.setText("Test Ereignis");
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

}