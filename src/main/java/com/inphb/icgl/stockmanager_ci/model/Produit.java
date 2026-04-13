package com.inphb.icgl.stockmanager_ci.model;

import javafx.beans.property.*;

/**
 * Modèle Produit — correspond à la table `produits`
 * Contient aussi nomCategorie et nomFournisseur (champs calculés via JOIN)
 * pour l'affichage dans les TableView sans chargement supplémentaire.
 */
public class Produit {

    private final IntegerProperty id              = new SimpleIntegerProperty();
    private final StringProperty  reference       = new SimpleStringProperty();
    private final StringProperty  designation     = new SimpleStringProperty();
    private final IntegerProperty idCategorie     = new SimpleIntegerProperty();
    private final IntegerProperty idFournisseur   = new SimpleIntegerProperty();
    private final DoubleProperty  prixUnitaire    = new SimpleDoubleProperty();
    private final IntegerProperty quantiteStock   = new SimpleIntegerProperty();
    private final IntegerProperty stockMinimum    = new SimpleIntegerProperty(5);
    private final StringProperty  unite           = new SimpleStringProperty("pièce");

    // Champs calculés (JOIN) — non stockés en BD
    private final StringProperty nomCategorie   = new SimpleStringProperty("");
    private final StringProperty nomFournisseur = new SimpleStringProperty("");

    public Produit() {}

    public Produit(int id, String reference, String designation,
                   int idCategorie, int idFournisseur,
                   double prixUnitaire, int quantiteStock, int stockMinimum,
                   String unite, String nomCategorie, String nomFournisseur) {
        this.id.set(id);
        this.reference.set(reference);
        this.designation.set(designation);
        this.idCategorie.set(idCategorie);
        this.idFournisseur.set(idFournisseur);
        this.prixUnitaire.set(prixUnitaire);
        this.quantiteStock.set(quantiteStock);
        this.stockMinimum.set(stockMinimum);
        this.unite.set(unite);
        this.nomCategorie.set(nomCategorie);
        this.nomFournisseur.set(nomFournisseur);
    }

    /** Vrai si le stock est en dessous ou égal au minimum */
    public boolean isEnAlerte() {
        return quantiteStock.get() <= stockMinimum.get();
    }

    // ── Getters / Setters / Properties ────────────────────────────

    public int getId()                     { return id.get(); }
    public void setId(int v)              { id.set(v); }
    public IntegerProperty idProperty()   { return id; }

    public String getReference()               { return reference.get(); }
    public void setReference(String v)         { reference.set(v); }
    public StringProperty referenceProperty()  { return reference; }

    public String getDesignation()              { return designation.get(); }
    public void setDesignation(String v)        { designation.set(v); }
    public StringProperty designationProperty() { return designation; }

    public int getIdCategorie()                    { return idCategorie.get(); }
    public void setIdCategorie(int v)              { idCategorie.set(v); }
    public IntegerProperty idCategorieProperty()   { return idCategorie; }

    public int getIdFournisseur()                   { return idFournisseur.get(); }
    public void setIdFournisseur(int v)             { idFournisseur.set(v); }
    public IntegerProperty idFournisseurProperty()  { return idFournisseur; }

    public double getPrixUnitaire()                { return prixUnitaire.get(); }
    public void setPrixUnitaire(double v)          { prixUnitaire.set(v); }
    public DoubleProperty prixUnitaireProperty()   { return prixUnitaire; }

    public int getQuantiteStock()                   { return quantiteStock.get(); }
    public void setQuantiteStock(int v)             { quantiteStock.set(v); }
    public IntegerProperty quantiteStockProperty()  { return quantiteStock; }

    public int getStockMinimum()                    { return stockMinimum.get(); }
    public void setStockMinimum(int v)              { stockMinimum.set(v); }
    public IntegerProperty stockMinimumProperty()   { return stockMinimum; }

    public String getUnite()                  { return unite.get(); }
    public void setUnite(String v)            { unite.set(v); }
    public StringProperty uniteProperty()     { return unite; }

    public String getNomCategorie()                  { return nomCategorie.get(); }
    public void setNomCategorie(String v)            { nomCategorie.set(v); }
    public StringProperty nomCategorieProperty()     { return nomCategorie; }

    public String getNomFournisseur()                { return nomFournisseur.get(); }
    public void setNomFournisseur(String v)          { nomFournisseur.set(v); }
    public StringProperty nomFournisseurProperty()   { return nomFournisseur; }

    @Override
    public String toString() { return "[" + reference.get() + "] " + designation.get(); }
}
