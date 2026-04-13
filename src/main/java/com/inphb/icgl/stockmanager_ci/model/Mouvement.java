package com.inphb.icgl.stockmanager_ci.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modèle Mouvement — correspond à la table `mouvements`
 */
public class Mouvement {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final IntegerProperty  id              = new SimpleIntegerProperty();
    private final IntegerProperty  idProduit       = new SimpleIntegerProperty();
    private final StringProperty   typeMouvement   = new SimpleStringProperty();
    private final IntegerProperty  quantite        = new SimpleIntegerProperty();
    private final StringProperty   motif           = new SimpleStringProperty();
    private final IntegerProperty  idUtilisateur   = new SimpleIntegerProperty();
    private       LocalDateTime    dateMouvement;

    // Champs calculés (JOIN)
    private final StringProperty nomProduit      = new SimpleStringProperty();
    private final StringProperty nomUtilisateur  = new SimpleStringProperty();

    public Mouvement() {}

    public Mouvement(int id, int idProduit, String typeMouvement,
                     int quantite, String motif, int idUtilisateur,
                     LocalDateTime dateMouvement,
                     String nomProduit, String nomUtilisateur) {
        this.id.set(id);
        this.idProduit.set(idProduit);
        this.typeMouvement.set(typeMouvement);
        this.quantite.set(quantite);
        this.motif.set(motif);
        this.idUtilisateur.set(idUtilisateur);
        this.dateMouvement = dateMouvement;
        this.nomProduit.set(nomProduit);
        this.nomUtilisateur.set(nomUtilisateur);
    }

    public int getId()                      { return id.get(); }
    public void setId(int v)               { id.set(v); }
    public IntegerProperty idProperty()    { return id; }

    public int getIdProduit()                       { return idProduit.get(); }
    public void setIdProduit(int v)                 { idProduit.set(v); }
    public IntegerProperty idProduitProperty()      { return idProduit; }

    public String getTypeMouvement()                     { return typeMouvement.get(); }
    public void setTypeMouvement(String v)               { typeMouvement.set(v); }
    public StringProperty typeMouvementProperty()        { return typeMouvement; }

    public int getQuantite()                        { return quantite.get(); }
    public void setQuantite(int v)                  { quantite.set(v); }
    public IntegerProperty quantiteProperty()       { return quantite; }

    public String getMotif()                  { return motif.get(); }
    public void setMotif(String v)            { motif.set(v); }
    public StringProperty motifProperty()     { return motif; }

    public int getIdUtilisateur()                        { return idUtilisateur.get(); }
    public void setIdUtilisateur(int v)                  { idUtilisateur.set(v); }
    public IntegerProperty idUtilisateurProperty()       { return idUtilisateur; }

    public LocalDateTime getDateMouvement()              { return dateMouvement; }
    public void setDateMouvement(LocalDateTime v)        { this.dateMouvement = v; }

    /** Retourne la date formatée pour affichage dans TableView */
    public String getDateFormatee() {
        return dateMouvement != null ? dateMouvement.format(FMT) : "";
    }

    public String getNomProduit()                  { return nomProduit.get(); }
    public void setNomProduit(String v)            { nomProduit.set(v); }
    public StringProperty nomProduitProperty()     { return nomProduit; }

    public String getNomUtilisateur()              { return nomUtilisateur.get(); }
    public void setNomUtilisateur(String v)        { nomUtilisateur.set(v); }
    public StringProperty nomUtilisateurProperty() { return nomUtilisateur; }
}
