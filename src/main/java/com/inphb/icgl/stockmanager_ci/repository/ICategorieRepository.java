package com.inphb.icgl.stockmanager_ci.repository;

import com.inphb.icgl.stockmanager_ci.model.Categorie;
import javafx.collections.ObservableList;

public interface ICategorieRepository {
    boolean save(Categorie c);
    boolean update(Categorie c);
    boolean delete(int id);
    Categorie findById(int id);
    ObservableList<Categorie> findAll(int page, int pageSize);
    int countAll();
    ObservableList<Categorie> search(String motCle, int page, int pageSize);
    int countSearch(String motCle);
    // Vérifie si des produits sont rattachés à cette catégorie
    int countProduitsLies(int idCategorie);
    // Retourne toutes les catégories sans pagination (pour les ComboBox)
    ObservableList<Categorie> findAllSansPagination();
}
