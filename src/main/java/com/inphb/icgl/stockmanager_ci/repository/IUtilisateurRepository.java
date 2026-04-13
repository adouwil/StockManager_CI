package com.inphb.icgl.stockmanager_ci.repository;

import com.inphb.icgl.stockmanager_ci.model.Utilisateur;
import javafx.collections.ObservableList;

public interface IUtilisateurRepository {
    boolean save(Utilisateur u);
    boolean update(Utilisateur u);
    boolean delete(int id);
    Utilisateur findById(int id);
    Utilisateur findByLogin(String login);
    Utilisateur authentifier(String login, String motDePasse);
    ObservableList<Utilisateur> findAll(int page, int pageSize);
    int countAll();
    ObservableList<Utilisateur> search(String motCle, int page, int pageSize);
    int countSearch(String motCle);
}
