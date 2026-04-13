package com.inphb.icgl.stockmanager_ci.dao;

import com.inphb.icgl.stockmanager_ci.model.Produit;
import com.inphb.icgl.stockmanager_ci.repository.IProduitRepository;
import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Implémentation SQL de IProduitRepository.
 * Les requêtes SELECT font un LEFT JOIN sur categories et fournisseurs
 * pour récupérer les noms en une seule requête.
 */
public class ProduitDAO implements IProduitRepository {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String SELECT_BASE =
        "SELECT p.*, c.libelle AS nom_categorie, f.nom AS nom_fournisseur " +
        "FROM produits p " +
        "LEFT JOIN categories   c ON p.id_categorie   = c.id " +
        "LEFT JOIN fournisseurs f ON p.id_fournisseur  = f.id ";

    @Override
    public boolean save(Produit p) {
        String sql = "INSERT INTO produits " +
                     "(reference, designation, id_categorie, id_fournisseur, " +
                     " prix_unitaire, quantite_stock, stock_minimum, unite) " +
                     "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getReference());
            ps.setString(2, p.getDesignation());
            setNullableInt(ps, 3, p.getIdCategorie());
            setNullableInt(ps, 4, p.getIdFournisseur());
            ps.setDouble(5, p.getPrixUnitaire());
            ps.setInt(6, p.getQuantiteStock());
            ps.setInt(7, p.getStockMinimum());
            ps.setString(8, p.getUnite());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Produit p) {
        String sql = "UPDATE produits SET reference=?, designation=?, id_categorie=?, " +
                     "id_fournisseur=?, prix_unitaire=?, quantite_stock=?, " +
                     "stock_minimum=?, unite=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getReference());
            ps.setString(2, p.getDesignation());
            setNullableInt(ps, 3, p.getIdCategorie());
            setNullableInt(ps, 4, p.getIdFournisseur());
            ps.setDouble(5, p.getPrixUnitaire());
            ps.setInt(6, p.getQuantiteStock());
            ps.setInt(7, p.getStockMinimum());
            ps.setString(8, p.getUnite());
            ps.setInt(9, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // Les mouvements liés sont supprimés via ON DELETE CASCADE
        String sql = "DELETE FROM produits WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Produit findById(int id) {
        String sql = SELECT_BASE + "WHERE p.id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Produit> findAll(int page, int pageSize) {
        String sql = SELECT_BASE + "ORDER BY p.designation LIMIT ? OFFSET ?";
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM produits");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Produit> search(String motCle, int page, int pageSize) {
        String sql = SELECT_BASE +
                     "WHERE p.designation LIKE ? OR p.reference LIKE ? " +
                     "   OR c.libelle LIKE ? OR f.nom LIKE ? " +
                     "ORDER BY p.designation LIMIT ? OFFSET ?";
        ObservableList<Produit> list = FXCollections.observableArrayList();
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            ps.setString(3, pattern); ps.setString(4, pattern);
            ps.setInt(5, pageSize);   ps.setInt(6, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String motCle) {
        String sql = "SELECT COUNT(*) FROM produits p " +
                     "LEFT JOIN categories   c ON p.id_categorie  = c.id " +
                     "LEFT JOIN fournisseurs f ON p.id_fournisseur = f.id " +
                     "WHERE p.designation LIKE ? OR p.reference LIKE ? " +
                     "   OR c.libelle LIKE ? OR f.nom LIKE ?";
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            ps.setString(3, pattern); ps.setString(4, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Produit> findEnAlerte() {
        String sql = SELECT_BASE +
                     "WHERE p.quantite_stock <= p.stock_minimum " +
                     "ORDER BY (p.quantite_stock * 1.0 / p.stock_minimum) ASC";
        ObservableList<Produit> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.findEnAlerte] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countEnAlerte() {
        String sql = "SELECT COUNT(*) FROM produits WHERE quantite_stock <= stock_minimum";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.countEnAlerte] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getValeurTotaleStock() {
        String sql = "SELECT COALESCE(SUM(quantite_stock * prix_unitaire), 0) FROM produits";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ProduitDAO.getValeurTotaleStock] " + e.getMessage());
        }
        return 0.0;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Produit mapRow(ResultSet rs) throws SQLException {
        return new Produit(
            rs.getInt("id"),
            rs.getString("reference"),
            rs.getString("designation"),
            rs.getInt("id_categorie"),
            rs.getInt("id_fournisseur"),
            rs.getDouble("prix_unitaire"),
            rs.getInt("quantite_stock"),
            rs.getInt("stock_minimum"),
            rs.getString("unite"),
            rs.getString("nom_categorie") != null ? rs.getString("nom_categorie") : "",
            rs.getString("nom_fournisseur") != null ? rs.getString("nom_fournisseur") : ""
        );
    }

    /** Insère NULL si l'id est 0 (non sélectionné dans la ComboBox) */
    private void setNullableInt(PreparedStatement ps, int index, int value) throws SQLException {
        if (value == 0) ps.setNull(index, Types.INTEGER);
        else            ps.setInt(index, value);
    }
}
