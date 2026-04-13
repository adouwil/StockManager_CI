package com.inphb.icgl.stockmanager_ci.repository;

import com.inphb.icgl.stockmanager_ci.model.Fournisseur;
import javafx.collections.ObservableList;

public interface IFournisseurRepository {
    boolean save(Fournisseur f);
    boolean update(Fournisseur f);
    boolean delete(int id);
    Fournisseur findById(int id);
    ObservableList<Fournisseur> findAll(int page, int pageSize);
    int countAll();
    ObservableList<Fournisseur> search(String motCle, int page, int pageSize);
    int countSearch(String motCle);
    int countProduitsLies(int idFournisseur);
    ObservableList<Fournisseur> findAllSansPagination();
}
