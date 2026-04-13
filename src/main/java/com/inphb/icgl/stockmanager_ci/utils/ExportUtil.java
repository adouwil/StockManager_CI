package com.inphb.icgl.stockmanager_ci.utils;

import com.inphb.icgl.stockmanager_ci.model.Produit;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utilitaire d'export XLSX (Excel) via Apache POI.
 * Produit un fichier .xlsx avec en-têtes colorés et lignes d'alerte en rouge.
 */
public class ExportUtil {

    private ExportUtil() {}

    /**
     * Exporte la liste des produits dans un fichier .xlsx.
     *
     * @param produits Liste des produits à exporter
     * @param fichier  Fichier de destination (choisi via FileChooser)
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void exporterXLSX(List<Produit> produits, File fichier) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Produits StockManager CI");

            //  Style en-tête : fond vert foncé, texte blanc, gras
            XSSFCellStyle styleEntete = workbook.createCellStyle();
            styleEntete.setFillForegroundColor(
                    new XSSFColor(new byte[]{(byte)27, (byte)94, (byte)32})); // #1B5E20
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleEntete.setAlignment(HorizontalAlignment.CENTER);
            styleEntete.setBorderBottom(BorderStyle.THIN);
            XSSFFont fontEntete = workbook.createFont();
            fontEntete.setColor(IndexedColors.WHITE.getIndex());
            fontEntete.setBold(true);
            fontEntete.setFontHeightInPoints((short) 11);
            styleEntete.setFont(fontEntete);

            //  Style alerte : fond rouge clair
            XSSFCellStyle styleAlerte = workbook.createCellStyle();
            styleAlerte.setFillForegroundColor(
                new XSSFColor(new byte[]{(byte)255, (byte)205, (byte)210})); // #FFCDD2
            styleAlerte.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            XSSFFont fontAlerte = workbook.createFont();
            fontAlerte.setColor(new XSSFColor(new byte[]{(byte)198, (byte)40, (byte)40}));
            fontAlerte.setBold(true);
            styleAlerte.setFont(fontAlerte);

            //  Style normal
            XSSFCellStyle styleNormal = workbook.createCellStyle();
            XSSFFont fontNormal = workbook.createFont();
            fontNormal.setFontHeightInPoints((short) 10);
            styleNormal.setFont(fontNormal);

            //  Ligne titre
            Row ligneTitre = sheet.createRow(0);
            Cell cellTitre = ligneTitre.createCell(0);
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            cellTitre.setCellValue("Export Produits — StockManager CI — " + timestamp);
            XSSFCellStyle styleTitre = workbook.createCellStyle();
            XSSFFont fontTitre = workbook.createFont();
            fontTitre.setBold(true);
            fontTitre.setFontHeightInPoints((short) 12);
            styleTitre.setFont(fontTitre);
            cellTitre.setCellStyle(styleTitre);

            //  Ligne d'en-tête
            String[] entetes = {
                "Référence", "Désignation", "Catégorie", "Fournisseur",
                "Prix (FCFA)", "Quantité", "Stock Min.", "Unité", "Alerte"
            };
            Row ligneEntete = sheet.createRow(1);
            for (int i = 0; i < entetes.length; i++) {
                Cell cell = ligneEntete.createCell(i);
                cell.setCellValue(entetes[i]);
                cell.setCellStyle(styleEntete);
                sheet.setColumnWidth(i, 5500);
            }
            // Largeurs ajustées
            sheet.setColumnWidth(1, 9000); // Désignation
            sheet.setColumnWidth(2, 5000); // Catégorie
            sheet.setColumnWidth(3, 7000); // Fournisseur

            //  Lignes de données
            int numLigne = 2;
            int nbAlertes = 0;
            for (Produit p : produits) {
                Row row = sheet.createRow(numLigne++);
                boolean enAlerte = p.isEnAlerte();
                if (enAlerte) nbAlertes++;

                Object[] valeurs = {
                    p.getReference(),
                    p.getDesignation(),
                    p.getNomCategorie(),
                    p.getNomFournisseur(),
                    p.getPrixUnitaire(),
                    p.getQuantiteStock(),
                    p.getStockMinimum(),
                    p.getUnite(),
                    enAlerte ? "⚠ OUI" : "NON"
                };

                for (int i = 0; i < valeurs.length; i++) {
                    Cell cell = row.createCell(i);
                    if (valeurs[i] instanceof Number) {
                        cell.setCellValue(((Number) valeurs[i]).doubleValue());
                    } else {
                        cell.setCellValue(String.valueOf(valeurs[i]));
                    }
                    cell.setCellStyle(enAlerte ? styleAlerte : styleNormal);
                }
            }

            // ── Ligne de résumé ────────────────────────────────────────────────
            Row ligneResume = sheet.createRow(numLigne + 1);
            Cell cellResume = ligneResume.createCell(0);
            cellResume.setCellValue(String.format(
                "Total : %d produits | %d en alerte de stock", produits.size(), nbAlertes));
            XSSFCellStyle styleResume = workbook.createCellStyle();
            XSSFFont fontResume = workbook.createFont();
            fontResume.setBold(true);
            fontResume.setItalic(true);
            styleResume.setFont(fontResume);
            cellResume.setCellStyle(styleResume);

            //  Enregistrement
            try (FileOutputStream fos = new FileOutputStream(fichier)) {
                workbook.write(fos);
            }
        }
    }
}
