package com.inphb.icgl.stockmanager_ci.dao;

import com.inphb.icgl.stockmanager_ci.model.Categorie;
import com.inphb.icgl.stockmanager_ci.repository.ICategorieRepository;
import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/*
  Implémentation SQL de ICategorieRepository.
  Toutes les requêtes utilisent PreparedStatement.
 */
public class CategorieDAO implements ICategorieRepository {
    //connexion à la base de données
    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    //Les méthodes CRUD
    @Override
    public boolean save(Categorie c) {
        String sql = "INSERT INTO categories (libelle, description) VALUES (?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getLibelle());
            ps.setString(2, c.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Categorie c) {
        String sql = "UPDATE categories SET libelle=?, description=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, c.getLibelle());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Categorie findById(int id) {
        String sql = "SELECT * FROM categories WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Categorie> findAll(int page, int pageSize) {
        String sql = "SELECT * FROM categories ORDER BY libelle LIMIT ? OFFSET ?";
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM categories";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Categorie> search(String motCle, int page, int pageSize) {
        String sql = "SELECT * FROM categories WHERE libelle LIKE ? OR description LIKE ? "
                   + "ORDER BY libelle LIMIT ? OFFSET ?";
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setInt(3, pageSize);
            ps.setInt(4, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String motCle) {
        String sql = "SELECT COUNT(*) FROM categories WHERE libelle LIKE ? OR description LIKE ?";
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countProduitsLies(int idCategorie) {
        String sql = "SELECT COUNT(*) FROM produits WHERE id_categorie=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idCategorie);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.countProduitsLies] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Categorie> findAllSansPagination() {
        String sql = "SELECT * FROM categories ORDER BY libelle";
        ObservableList<Categorie> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategorieDAO.findAllSansPagination] " + e.getMessage());
        }
        return list;
    }

    private Categorie mapRow(ResultSet rs) throws SQLException {
        return new Categorie(
            rs.getInt("id"),
            rs.getString("libelle"),
            rs.getString("description")
        );
    }
}
