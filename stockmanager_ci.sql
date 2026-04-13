-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : lun. 13 avr. 2026 à 22:46
-- Version du serveur : 8.4.7
-- Version de PHP : 8.4.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `stockmanager_ci`
--

-- --------------------------------------------------------

--
-- Structure de la table `categories`
--

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `libelle` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `libelle` (`libelle`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `categories`
--

INSERT INTO `categories` (`id`, `libelle`, `description`) VALUES
(1, 'Alimentaire', 'Produits alimentaires et boissons'),
(2, 'Quincaillerie', 'Matériaux de construction et outils'),
(3, 'Papeterie', 'Fournitures scolaires et de bureau'),
(4, 'Électronique', 'Appareils et accessoires électroniques'),
(5, 'Médicament', 'Produits pharmaceutiques et hygiène'),
(6, 'Cosmétiques', 'Produits de soins et de beauté'),
(8, 'Cuisine', 'Ustensiles et Produits de cuisines');

-- --------------------------------------------------------

--
-- Structure de la table `fournisseurs`
--

DROP TABLE IF EXISTS `fournisseurs`;
CREATE TABLE IF NOT EXISTS `fournisseurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `telephone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `adresse` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ville` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `fournisseurs`
--

INSERT INTO `fournisseurs` (`id`, `nom`, `telephone`, `email`, `adresse`, `ville`) VALUES
(1, 'SIFCA Distribution', '27 20 25 00 00', 'contact@sifca.ci', NULL, 'Abidjan'),
(2, 'SIMAT Quincaillerie', '27 20 31 12 00', 'simat@simat.ci', NULL, 'Abidjan'),
(3, 'CFAO Technologies', '27 21 00 11 00', 'cfao@cfao.ci', NULL, 'Abidjan'),
(4, 'Librairie de France CI', '27 20 22 50 00', 'ldf@librairie.ci', NULL, 'Abidjan'),
(5, 'COPHARMED', '27 20 37 40 00', 'copharmed@cop.ci', NULL, 'Abidjan'),
(6, 'PROSUMA', '27 21 25 34 16', 'contact@groupeprosuma.com', 'Rue des carossiers', 'Abidjan'),
(8, 'SOCIAM CI', '27 20 27 26 33', 'infos@sociam.net', 'Yopougon - Zone Industrielle', 'Abidjan');

-- --------------------------------------------------------

--
-- Structure de la table `mouvements`
--

DROP TABLE IF EXISTS `mouvements`;
CREATE TABLE IF NOT EXISTS `mouvements` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_produit` int NOT NULL,
  `type_mouvement` enum('ENTREE','SORTIE') COLLATE utf8mb4_general_ci NOT NULL,
  `quantite` int NOT NULL,
  `motif` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_utilisateur` int DEFAULT NULL,
  `date_mouvement` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_produit` (`id_produit`),
  KEY `id_utilisateur` (`id_utilisateur`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `mouvements`
--

INSERT INTO `mouvements` (`id`, `id_produit`, `type_mouvement`, `quantite`, `motif`, `id_utilisateur`, `date_mouvement`) VALUES
(1, 1, 'SORTIE', 50, 'Vente', 2, '2026-04-03 11:02:32'),
(3, 8, 'ENTREE', 20, 'Augmenter Stock', 3, '2026-04-03 11:08:44'),
(4, 11, 'ENTREE', 30, 'Augmenter Stock', 3, '2026-04-03 11:13:01'),
(5, 14, 'ENTREE', 15, 'Commande stock', 2, '2026-04-13 22:17:16'),
(6, 5, 'SORTIE', 22, 'Vente', 2, '2026-04-13 22:18:49'),
(7, 2, 'SORTIE', 43, 'Vente Client', 2, '2026-04-13 22:21:21');

-- --------------------------------------------------------

--
-- Structure de la table `produits`
--

DROP TABLE IF EXISTS `produits`;
CREATE TABLE IF NOT EXISTS `produits` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reference` varchar(30) COLLATE utf8mb4_general_ci NOT NULL,
  `designation` varchar(200) COLLATE utf8mb4_general_ci NOT NULL,
  `id_categorie` int DEFAULT NULL,
  `id_fournisseur` int DEFAULT NULL,
  `prix_unitaire` decimal(12,2) NOT NULL DEFAULT '0.00',
  `quantite_stock` int NOT NULL DEFAULT '0',
  `stock_minimum` int NOT NULL DEFAULT '5',
  `unite` varchar(30) COLLATE utf8mb4_general_ci DEFAULT 'pièce',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `reference` (`reference`),
  KEY `id_categorie` (`id_categorie`),
  KEY `id_fournisseur` (`id_fournisseur`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `produits`
--

INSERT INTO `produits` (`id`, `reference`, `designation`, `id_categorie`, `id_fournisseur`, `prix_unitaire`, `quantite_stock`, `stock_minimum`, `unite`, `created_at`) VALUES
(1, 'ALI-001', 'Riz Rizière 5Kg', 1, 6, 3000.00, 450, 200, 'pièce', '2026-04-03 09:33:52'),
(2, 'ALI-002', 'Huile Dinor 1,5L', 1, 6, 1700.00, 147, 80, 'pièce', '2026-04-03 09:35:50'),
(3, 'ELC-001', 'Télévision TCL 50\" Android', 4, 3, 200000.00, 10, 3, 'pièce', '2026-04-03 09:38:26'),
(4, 'ELC-002', 'SAMSUNG A16 (RAM 4Go, ROM 128Go)', 4, 3, 90000.00, 15, 5, 'pièce', '2026-04-03 09:48:24'),
(5, 'COS-001', 'Parfum Monark', 6, 6, 8000.00, 8, 10, 'pièce', '2026-04-03 09:53:10'),
(6, 'ALI-003', 'Saucisson 400G', 1, 6, 2800.00, 100, 40, 'pièce', '2026-04-03 09:54:44'),
(7, 'PAP-001', 'Registre 12 Mains 1000 pages', 3, 4, 15000.00, 100, 25, 'pièce', '2026-04-03 10:10:26'),
(8, 'ELC-003', 'Ventilateur Crown 18\"', 2, 6, 12000.00, 50, 10, 'pièce', '2026-04-03 10:38:30'),
(9, 'ALI-004', 'Vin Bordeaux 75cl', 1, 6, 4000.00, 200, 50, 'pièce', '2026-04-03 10:39:20'),
(10, 'ALI-005', 'Bière Beaufort 25cl', 1, 6, 400.00, 500, 200, 'pièce', '2026-04-03 10:44:32'),
(11, 'COS-002', 'Savon Roger Cavaillès', 6, 6, 3000.00, 80, 20, 'pièce', '2026-04-03 11:12:01'),
(12, 'PAP-002', 'Cahier Etudiant 200 pages', 3, 4, 700.00, 200, 100, 'pièce', '2026-04-12 10:41:44'),
(13, 'ELC-004', 'Réfrigerateur NASCO 225L', 4, 8, 180000.00, 0, 5, 'pièce', '2026-04-13 22:02:54'),
(14, 'CUI-001', 'Cuisinière 4 feux NASCO', 8, 8, 55000.00, 18, 5, 'pièce', '2026-04-13 22:09:42'),
(15, 'CUI-002', 'Assiettes ceramic', 8, 2, 500.00, 80, 50, 'pièce', '2026-04-13 22:11:30'),
(16, 'CUI-003', 'Lot de Soupière TEFAL', 8, 8, 45000.00, 0, 3, 'pièce', '2026-04-13 22:13:33'),
(17, 'ELC-005', 'Blender Nasco', 4, 8, 6750.00, 50, 15, 'pièce', '2026-04-13 22:25:20');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateurs`
--

DROP TABLE IF EXISTS `utilisateurs`;
CREATE TABLE IF NOT EXISTS `utilisateurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom_complet` varchar(150) COLLATE utf8mb4_general_ci NOT NULL,
  `login` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `mot_de_passe` varchar(64) COLLATE utf8mb4_general_ci NOT NULL,
  `role` enum('ADMIN','GESTIONNAIRE') COLLATE utf8mb4_general_ci DEFAULT 'GESTIONNAIRE',
  `actif` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateurs`
--

INSERT INTO `utilisateurs` (`id`, `nom_complet`, `login`, `mot_de_passe`, `role`, `actif`, `created_at`) VALUES
(1, 'Administrateur Système', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 1, '2026-03-30 08:15:14'),
(2, 'Kouamé Gestionnaire', 'kouame', '90933df41ce5ec368e13e971f390bdb10e23d1f45b74d9552e6cb6a2ef524eaf', 'GESTIONNAIRE', 1, '2026-03-30 08:15:14'),
(3, 'Wilfried ADOU', 'Wilfried', 'fcb11f5415dcd68ff4f2a415cda90a9bc871501b21b1298c55b9a9ba5df3e4e7', 'GESTIONNAIRE', 1, '2026-04-03 11:06:21');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
