package com.inphb.icgl.stockmanager_ci.dao;

import com.inphb.icgl.stockmanager_ci.model.Fournisseur;
import com.inphb.icgl.stockmanager_ci.repository.IFournisseurRepository;
import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class FournisseurDAO implements IFournisseurRepository {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean save(Fournisseur f) {
        String sql = "INSERT INTO fournisseurs (nom, telephone, email, adresse, ville) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.setString(5, f.getVille());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Fournisseur f) {
        String sql = "UPDATE fournisseurs SET nom=?, telephone=?, email=?, adresse=?, ville=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, f.getNom());
            ps.setString(2, f.getTelephone());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getAdresse());
            ps.setString(5, f.getVille());
            ps.setInt(6, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM fournisseurs WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Fournisseur findById(int id) {
        String sql = "SELECT * FROM fournisseurs WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Fournisseur> findAll(int page, int pageSize) {
        String sql = "SELECT * FROM fournisseurs ORDER BY nom LIMIT ? OFFSET ?";
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM fournisseurs");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Fournisseur> search(String motCle, int page, int pageSize) {
        String sql = "SELECT * FROM fournisseurs WHERE nom LIKE ? OR telephone LIKE ? OR ville LIKE ? "
                   + "ORDER BY nom LIMIT ? OFFSET ?";
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
            ps.setInt(4, pageSize); ps.setInt(5, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String motCle) {
        String sql = "SELECT COUNT(*) FROM fournisseurs WHERE nom LIKE ? OR telephone LIKE ? OR ville LIKE ?";
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countProduitsLies(int idFournisseur) {
        String sql = "SELECT COUNT(*) FROM produits WHERE id_fournisseur=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idFournisseur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.countProduitsLies] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Fournisseur> findAllSansPagination() {
        String sql = "SELECT * FROM fournisseurs ORDER BY nom";
        ObservableList<Fournisseur> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[FournisseurDAO.findAllSansPagination] " + e.getMessage());
        }
        return list;
    }

    private Fournisseur mapRow(ResultSet rs) throws SQLException {
        return new Fournisseur(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("telephone"),
            rs.getString("email"),
            rs.getString("adresse"),
            rs.getString("ville")
        );
    }
}
