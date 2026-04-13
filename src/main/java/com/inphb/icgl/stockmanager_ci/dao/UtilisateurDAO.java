package com.inphb.icgl.stockmanager_ci.dao;

import com.inphb.icgl.stockmanager_ci.model.Utilisateur;
import com.inphb.icgl.stockmanager_ci.repository.IUtilisateurRepository;
import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import com.inphb.icgl.stockmanager_ci.utils.HashUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class UtilisateurDAO implements IUtilisateurRepository {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean save(Utilisateur u) {
        String sql = "INSERT INTO utilisateurs (nom_complet, login, mot_de_passe, role, actif) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNomComplet());
            ps.setString(2, u.getLogin());
            ps.setString(3, HashUtil.sha256(u.getMotDePasse())); // Toujours hasher
            ps.setString(4, u.getRole());
            ps.setInt(5, u.isActif() ? 1 : 0);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.save] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Utilisateur u) {
        // Si motDePasse est vide → ne pas le modifier
        String sql;
        if (u.getMotDePasse() != null && !u.getMotDePasse().isBlank()) {
            sql = "UPDATE utilisateurs SET nom_complet=?, login=?, mot_de_passe=?, role=?, actif=? WHERE id=?";
        } else {
            sql = "UPDATE utilisateurs SET nom_complet=?, login=?, role=?, actif=? WHERE id=?";
        }
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNomComplet());
            ps.setString(2, u.getLogin());
            if (u.getMotDePasse() != null && !u.getMotDePasse().isBlank()) {
                ps.setString(3, HashUtil.sha256(u.getMotDePasse()));
                ps.setString(4, u.getRole());
                ps.setInt(5, u.isActif() ? 1 : 0);
                ps.setInt(6, u.getId());
            } else {
                ps.setString(3, u.getRole());
                ps.setInt(4, u.isActif() ? 1 : 0);
                ps.setInt(5, u.getId());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.update] " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.delete] " + e.getMessage());
            return false;
        }
    }

    @Override
    public Utilisateur findById(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findById] " + e.getMessage());
        }
        return null;
    }

    @Override
    public Utilisateur findByLogin(String login) {
        String sql = "SELECT * FROM utilisateurs WHERE login=? AND actif=1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findByLogin] " + e.getMessage());
        }
        return null;
    }

    /**
     * Authentifie un utilisateur.
     * Le mot de passe est hashé en SHA-256 avant comparaison.
     * Retourne l'utilisateur si les identifiants sont corrects, null sinon.
     */
    @Override
    public Utilisateur authentifier(String login, String motDePasse) {
        String sql = "SELECT * FROM utilisateurs WHERE login=? AND mot_de_passe=? AND actif=1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, HashUtil.sha256(motDePasse));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.authentifier] " + e.getMessage());
        }
        return null;
    }

    @Override
    public ObservableList<Utilisateur> findAll(int page, int pageSize) {
        String sql = "SELECT * FROM utilisateurs ORDER BY nom_complet LIMIT ? OFFSET ?";
        ObservableList<Utilisateur> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM utilisateurs");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Utilisateur> search(String motCle, int page, int pageSize) {
        String sql = "SELECT * FROM utilisateurs WHERE nom_complet LIKE ? OR login LIKE ? " +
                     "ORDER BY nom_complet LIMIT ? OFFSET ?";
        ObservableList<Utilisateur> list = FXCollections.observableArrayList();
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            ps.setInt(3, pageSize);   ps.setInt(4, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String motCle) {
        String sql = "SELECT COUNT(*) FROM utilisateurs WHERE nom_complet LIKE ? OR login LIKE ?";
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[UtilisateurDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    private Utilisateur mapRow(ResultSet rs) throws SQLException {
        return new Utilisateur(
            rs.getInt("id"),
            rs.getString("nom_complet"),
            rs.getString("login"),
            rs.getString("mot_de_passe"),
            rs.getString("role"),
            rs.getInt("actif") == 1
        );
    }
}
