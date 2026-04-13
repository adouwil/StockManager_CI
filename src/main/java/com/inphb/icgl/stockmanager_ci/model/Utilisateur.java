package com.inphb.icgl.stockmanager_ci.model;

import javafx.beans.property.*;

/**
 * Modèle Utilisateur — correspond à la table `utilisateurs`
 * Utilise les JavaFX Properties pour le binding avec les TableView.
 */
public class Utilisateur {

    private final IntegerProperty id           = new SimpleIntegerProperty();
    private final StringProperty  nomComplet   = new SimpleStringProperty();
    private final StringProperty  login        = new SimpleStringProperty();
    private final StringProperty  motDePasse   = new SimpleStringProperty();
    private final StringProperty  role         = new SimpleStringProperty();
    private final BooleanProperty actif        = new SimpleBooleanProperty(true);

    // ── Constructeurs ──────────────────────────────────────────────
    public Utilisateur() {}

    public Utilisateur(int id, String nomComplet, String login,
                       String motDePasse, String role, boolean actif) {
        this.id.set(id);
        this.nomComplet.set(nomComplet);
        this.login.set(login);
        this.motDePasse.set(motDePasse);
        this.role.set(role);
        this.actif.set(actif);
    }

    // ── Getters / Setters / Properties ────────────────────────────
    public int getId()                    { return id.get(); }
    public void setId(int v)             { id.set(v); }
    public IntegerProperty idProperty()  { return id; }

    public String getNomComplet()                   { return nomComplet.get(); }
    public void setNomComplet(String v)            { nomComplet.set(v); }
    public StringProperty nomCompletProperty()     { return nomComplet; }

    public String getLogin()                        { return login.get(); }
    public void setLogin(String v)                 { login.set(v); }
    public StringProperty loginProperty()          { return login; }

    public String getMotDePasse()                   { return motDePasse.get(); }
    public void setMotDePasse(String v)            { motDePasse.set(v); }
    public StringProperty motDePasseProperty()     { return motDePasse; }

    public String getRole()                         { return role.get(); }
    public void setRole(String v)                  { role.set(v); }
    public StringProperty roleProperty()           { return role; }

    public boolean isActif()                        { return actif.get(); }
    public void setActif(boolean v)                { actif.set(v); }
    public BooleanProperty actifProperty()         { return actif; }

    public String getStatutLabel() { return actif.get() ? "Actif" : "Inactif"; }

    @Override
    public String toString() { return nomComplet.get() + " (" + login.get() + ")"; }
}
