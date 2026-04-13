package com.inphb.icgl.stockmanager_ci.dao;

import com.inphb.icgl.stockmanager_ci.model.Mouvement;
import com.inphb.icgl.stockmanager_ci.repository.IMouvementRepository;
import com.inphb.icgl.stockmanager_ci.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Implémentation SQL de IMouvementRepository.
 * Les mouvements sont en lecture seule (pas de UPDATE / DELETE).
 * La mise à jour du stock est faite dans la même transaction.
 */
public class MouvementDAO implements IMouvementRepository {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    private static final String SELECT_BASE =
        "SELECT m.*, p.designation AS nom_produit, u.nom_complet AS nom_utilisateur " +
        "FROM mouvements m " +
        "LEFT JOIN produits      p ON m.id_produit     = p.id " +
        "LEFT JOIN utilisateurs  u ON m.id_utilisateur = u.id ";

    /**
     * Enregistre un mouvement ET met à jour la quantite_stock du produit
     * dans la même transaction JDBC pour garantir la cohérence.
     */
    @Override
    public boolean save(Mouvement m) {
        Connection conn = null;
        try {
            conn = getConn();
            conn.setAutoCommit(false);  // Début de transaction

            // 1. Insérer le mouvement
            String sqlMvt = "INSERT INTO mouvements " +
                            "(id_produit, type_mouvement, quantite, motif, id_utilisateur) " +
                            "VALUES (?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlMvt)) {
                ps.setInt(1, m.getIdProduit());
                ps.setString(2, m.getTypeMouvement());
                ps.setInt(3, m.getQuantite());
                ps.setString(4, m.getMotif());
                if (m.getIdUtilisateur() > 0)
                    ps.setInt(5, m.getIdUtilisateur());
                else
                    ps.setNull(5, Types.INTEGER);
                ps.executeUpdate();
            }

            // 2. Mettre à jour la quantité du produit
            String delta = "ENTREE".equals(m.getTypeMouvement()) ? "+" : "-";
            String sqlStock = "UPDATE produits SET quantite_stock = quantite_stock " +
                              delta + " ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlStock)) {
                ps.setInt(1, m.getQuantite());
                ps.setInt(2, m.getIdProduit());
                ps.executeUpdate();
            }

            conn.commit();  // Valider la transaction
            return true;

        } catch (SQLException e) {
            System.err.println("[MouvementDAO.save] " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignore */ }
            }
        }
    }

    @Override
    public ObservableList<Mouvement> findAll(int page, int pageSize) {
        String sql = SELECT_BASE + "ORDER BY m.date_mouvement DESC LIMIT ? OFFSET ?";
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, pageSize);
            ps.setInt(2, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.findAll] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countAll() {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT COUNT(*) FROM mouvements");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countAll] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Mouvement> search(String motCle, int page, int pageSize) {
        String sql = SELECT_BASE +
                     "WHERE p.designation LIKE ? OR m.motif LIKE ? " +
                     "   OR m.type_mouvement LIKE ? " +
                     "ORDER BY m.date_mouvement DESC LIMIT ? OFFSET ?";
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
            ps.setInt(4, pageSize);   ps.setInt(5, (page - 1) * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.search] " + e.getMessage());
        }
        return list;
    }

    @Override
    public int countSearch(String motCle) {
        String sql = "SELECT COUNT(*) FROM mouvements m " +
                     "LEFT JOIN produits p ON m.id_produit = p.id " +
                     "WHERE p.designation LIKE ? OR m.motif LIKE ? OR m.type_mouvement LIKE ?";
        String pattern = "%" + motCle + "%";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countSearch] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countMouvementsAujourdhui() {
        String sql = "SELECT COUNT(*) FROM mouvements WHERE DATE(date_mouvement) = CURDATE()";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.countMouvementsAujourdhui] " + e.getMessage());
        }
        return 0;
    }

    @Override
    public ObservableList<Mouvement> findRecents(int limite) {
        String sql = SELECT_BASE + "ORDER BY m.date_mouvement DESC LIMIT ?";
        ObservableList<Mouvement> list = FXCollections.observableArrayList();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MouvementDAO.findRecents] " + e.getMessage());
        }
        return list;
    }

    private Mouvement mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("date_mouvement");
        LocalDateTime date = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        return new Mouvement(
            rs.getInt("id"),
            rs.getInt("id_produit"),
            rs.getString("type_mouvement"),
            rs.getInt("quantite"),
            rs.getString("motif"),
            rs.getInt("id_utilisateur"),
            date,
            rs.getString("nom_produit")     != null ? rs.getString("nom_produit")     : "",
            rs.getString("nom_utilisateur") != null ? rs.getString("nom_utilisateur") : ""
        );
    }
}
