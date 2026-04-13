package com.inphb.icgl.stockmanager_ci.model;

import javafx.beans.property.*;

/**
 * Modèle Categorie — correspond à la table `categories`
 */
public class Categorie {

    private final IntegerProperty id          = new SimpleIntegerProperty();
    private final StringProperty  libelle     = new SimpleStringProperty();
    private final StringProperty  description = new SimpleStringProperty();

    public Categorie() {}

    public Categorie(int id, String libelle, String description) {
        this.id.set(id);
        this.libelle.set(libelle);
        this.description.set(description);
    }

    public int getId()                       { return id.get(); }
    public void setId(int v)                { id.set(v); }
    public IntegerProperty idProperty()     { return id; }

    public String getLibelle()                    { return libelle.get(); }
    public void setLibelle(String v)             { libelle.set(v); }
    public StringProperty libelleProperty()      { return libelle; }

    public String getDescription()               { return description.get(); }
    public void setDescription(String v)         { description.set(v); }
    public StringProperty descriptionProperty()  { return description; }

    // Utilisé par les ComboBox
    @Override
    public String toString() { return libelle.get(); }
}
