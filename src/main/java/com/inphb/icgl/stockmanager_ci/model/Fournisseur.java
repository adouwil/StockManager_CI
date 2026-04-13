package com.inphb.icgl.stockmanager_ci.model;

import javafx.beans.property.*;

/**
 * Modèle Fournisseur — correspond à la table `fournisseurs`
 */
public class Fournisseur {

    private final IntegerProperty id        = new SimpleIntegerProperty();
    private final StringProperty  nom       = new SimpleStringProperty();
    private final StringProperty  telephone = new SimpleStringProperty();
    private final StringProperty  email     = new SimpleStringProperty();
    private final StringProperty  adresse   = new SimpleStringProperty();
    private final StringProperty  ville     = new SimpleStringProperty();

    public Fournisseur() {}

    public Fournisseur(int id, String nom, String telephone,
                       String email, String adresse, String ville) {
        this.id.set(id);
        this.nom.set(nom);
        this.telephone.set(telephone);
        this.email.set(email);
        this.adresse.set(adresse);
        this.ville.set(ville);
    }

    public int getId()                    { return id.get(); }
    public void setId(int v)             { id.set(v); }
    public IntegerProperty idProperty()  { return id; }

    public String getNom()                    { return nom.get(); }
    public void setNom(String v)             { nom.set(v); }
    public StringProperty nomProperty()      { return nom; }

    public String getTelephone()              { return telephone.get(); }
    public void setTelephone(String v)        { telephone.set(v); }
    public StringProperty telephoneProperty() { return telephone; }

    public String getEmail()                  { return email.get(); }
    public void setEmail(String v)            { email.set(v); }
    public StringProperty emailProperty()     { return email; }

    public String getAdresse()                { return adresse.get(); }
    public void setAdresse(String v)          { adresse.set(v); }
    public StringProperty adresseProperty()   { return adresse; }

    public String getVille()                  { return ville.get(); }
    public void setVille(String v)            { ville.set(v); }
    public StringProperty villeProperty()     { return ville; }

    @Override
    public String toString() { return nom.get() + " — " + ville.get(); }
}
