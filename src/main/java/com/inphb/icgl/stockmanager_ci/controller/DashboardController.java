package com.inphb.icgl.stockmanager_ci.controller;

import com.inphb.icgl.stockmanager_ci.dao.*;
import com.inphb.icgl.stockmanager_ci.model.Mouvement;
import com.inphb.icgl.stockmanager_ci.model.Produit;
import com.inphb.icgl.stockmanager_ci.repository.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.text.NumberFormat;
import java.util.Locale;

/*
 Contrôleur du tableau de bord.
 Affiche : KPIs, produits critiques, derniers mouvements.
 */
public class DashboardController {

    // KPI Labels
    @FXML private Label lblTotalProduits;
    @FXML private Label lblProduitsAlerte;
    @FXML private Label lblMouvementsJour;
    @FXML private Label lblValeurStock;
    @FXML private Label lblCritiques;
    @FXML private Label lblDashboard;
    @FXML private Label lblDerMouv;

    //  TableView produits critiques
    @FXML private TableView<Produit>          tblCritiques;
    @FXML private TableColumn<Produit,String> colCritRef;
    @FXML private TableColumn<Produit,String> colCritDesig;
    @FXML private TableColumn<Produit,Integer> colCritQte;
    @FXML private TableColumn<Produit,Integer> colCritMin;
    @FXML private TableColumn<Produit,String> colCritUnite;

    // TableView des derniers mouvements
    @FXML private TableView<Mouvement>           tblMouvements;
    @FXML private TableColumn<Mouvement,String>  colMvtDate;
    @FXML private TableColumn<Mouvement,String>  colMvtProduit;
    @FXML private TableColumn<Mouvement,String>  colMvtType;
    @FXML private TableColumn<Mouvement,Integer> colMvtQte;
    @FXML private TableColumn<Mouvement,String>  colMvtMotif;

    private final IProduitRepository    produitRepo    = new ProduitDAO();
    private final IMouvementRepository  mouvementRepo  = new MouvementDAO();

    private static final NumberFormat FORMAT_FCFA =
        NumberFormat.getNumberInstance(Locale.FRANCE);

    @FXML
    public void initialize() {
        // Gestion de l'icone du Dashboard
        FontIcon iconDashboard = new FontIcon("mdi2h-home");
        lblDashboard.setGraphic(iconDashboard);
        ((FontIcon) lblDashboard.getGraphic()).setIconSize(30);
        iconDashboard.setIconColor(Color.web("#0C447C"));

        lblCritiques.setGraphic(new FontIcon("mdi2a-alert-outline"));
        lblDerMouv.setGraphic(new FontIcon("mdi2s-swap-horizontal"));
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        // Produits critiques
        colCritRef.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colCritDesig.setCellValueFactory(new PropertyValueFactory<>("designation"));
        colCritQte.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colCritMin.setCellValueFactory(new PropertyValueFactory<>("stockMinimum"));
        colCritUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));

        // Colorer en rouge les lignes dont le stock est en alerte
        tblCritiques.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                if (p == null || empty) setStyle("");
                else if (p.isEnAlerte()) setStyle("-fx-background-color: #FFCDD2;");
                else setStyle("");
            }
        });

        // Derniers mouvements
        colMvtDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        colMvtProduit.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colMvtType.setCellValueFactory(new PropertyValueFactory<>("typeMouvement"));
        colMvtQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colMvtMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
    }

    private void chargerDonnees() {
        // KPIs
        int totalProduits   = produitRepo.countAll();
        int produitsAlerte  = produitRepo.countEnAlerte();
        int mvtJour         = mouvementRepo.countMouvementsAujourdhui();
        double valeurStock  = produitRepo.getValeurTotaleStock();

        lblTotalProduits.setText(String.valueOf(totalProduits));
        lblProduitsAlerte.setText(String.valueOf(produitsAlerte));
        lblMouvementsJour.setText(String.valueOf(mvtJour));
        lblValeurStock.setText(FORMAT_FCFA.format((long) valeurStock) + " FCFA");

        // Couleur rouge si alerte
        if (produitsAlerte > 0) {
            lblProduitsAlerte.setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold;");
        }

        // Top 5 produits critiques
        tblCritiques.setItems(produitRepo.findEnAlerte());

        // 8 derniers mouvements
        tblMouvements.setItems(mouvementRepo.findRecents(8));
    }

    @FXML
    private void handleRafraichir() {
        chargerDonnees();
    }
}
