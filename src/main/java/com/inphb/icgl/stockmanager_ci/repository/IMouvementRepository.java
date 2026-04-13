package com.inphb.icgl.stockmanager_ci.repository;

import com.inphb.icgl.stockmanager_ci.model.Mouvement;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public interface IMouvementRepository {
    boolean save(Mouvement m);
    ObservableList<Mouvement> findAll(int page, int pageSize);
    int countAll();
    ObservableList<Mouvement> search(String motCle, int page, int pageSize);
    int countSearch(String motCle);
    int countMouvementsAujourdhui();
    ObservableList<Mouvement> findRecents(int limite);
}
