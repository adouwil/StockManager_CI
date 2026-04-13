package com.inphb.icgl.stockmanager_ci.repository;

import com.inphb.icgl.stockmanager_ci.model.Produit;
import javafx.collections.ObservableList;

public interface IProduitRepository {
    boolean save(Produit p);
    boolean update(Produit p);
    boolean delete(int id);
    Produit findById(int id);
    ObservableList<Produit> findAll(int page, int pageSize);
    int countAll();
    ObservableList<Produit> search(String motCle, int page, int pageSize);
    int countSearch(String motCle);
    ObservableList<Produit> findEnAlerte();
    int countEnAlerte();
    double getValeurTotaleStock();
}
