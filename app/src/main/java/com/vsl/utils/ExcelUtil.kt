package com.vsl.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.vsl.data.db.Patient
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

object ExcelUtil {
    fun readPatientsFromExcel(context: Context, fileUri: Uri): List<Patient> {
        val patients = mutableListOf<Patient>()
        val contentResolver = context.contentResolver
        val sheetName = "Planning"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

        try {
            val inputStream = contentResolver.openInputStream(fileUri) ?: return emptyList()
            inputStream.use { stream ->
                val workbook = WorkbookFactory.create(stream)
                val sheet = workbook.getSheet(sheetName) ?: return emptyList()
                // Données à partir de la ligne 7 (index 6)
                for (rowIndex in 6..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue
                    val nom = getCellString(row.getCell(2)).trim()
                    if (nom.isEmpty()) continue
                    val patient = Patient(
                        nom = nom,
                        dateNaissance = getCellAsDateOrString(row.getCell(3)),
                        dep = getCellString(row.getCell(4)),
                        da = getCellString(row.getCell(5)),
                        validiteDaDu = getCellAsDateOrString(row.getCell(6)),
                        validiteDaAu = getCellAsDateOrString(row.getCell(7)),
                        transportDu = getCellAsDateOrString(row.getCell(8)),
                        transportAu = getCellAsDateOrString(row.getCell(9)),
                        adresseGeographique = getCellString(row.getCell(10)),
                        trajet = getCellString(row.getCell(11)),
                        pointPcArrivee = getCellString(row.getCell(12)),
                        complementAdresse = getCellString(row.getCell(13)),
                        tel = getCellString(row.getCell(14)),
                        dn = getCellString(row.getCell(15)),
                        pk = getCellAsNumberOrString(row.getCell(16)),
                        kmSuppl = getCellAsNumberOrString(row.getCell(17)),
                        pkCentreSoin = getCellAsNumberOrString(row.getCell(18)),
                        tf = getCellString(row.getCell(19))
                    )
                    patients.add(patient)
                }
            }
        } catch (e: Exception) {
            Log.e("ExcelUtil", "Erreur lors de la lecture Excel", e)
            return emptyList()
        }
        return patients
    }

    fun writePatientsToExcel(context: Context, fileUri: Uri, patients: List<Patient>) {
        val contentResolver = context.contentResolver
        try {
            // Ouvre le fichier existant ou crée un nouveau workbook
            val workbook = try {
                val inStream = contentResolver.openInputStream(fileUri)
                inStream?.use { WorkbookFactory.create(it) } ?: XSSFWorkbook()
            } catch (e: Exception) {
                XSSFWorkbook()
            }
            val sheetName = "Planning"
            val sheet = workbook.getSheet(sheetName) ?: workbook.createSheet(sheetName)

            // Écrit l'en-tête si la feuille est vide
            if (sheet.lastRowNum == 0 && sheet.getRow(0) == null) {
                val headers = listOf(
                    "Nom", "DateNaissance", "DEP", "DA", "Validité DA du", "Validité DA au", "Transport du", "Transport au",
                    "Adresse géographique", "Trajet", "Point PC arrivée", "Complément adresse", "Tel",
                    "DN", "PK", "Km suppl", "PK centre soin", "TF"
                )
                val headerRow = sheet.createRow(0)
                headers.forEachIndexed { idx, h -> headerRow.createCell(idx).setCellValue(h) }
            }

            // Récupère les noms déjà présents pour éviter les doublons
            val existingNames = mutableSetOf<String>()
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                val cell = row.getCell(0) ?: continue
                val nomCellVal = cell.stringCellValue?.trim()?.uppercase() ?: ""
                if (nomCellVal.isNotEmpty()) existingNames.add(nomCellVal)
            }

            // Trie alphabétique et ajout des nouveaux patients uniquement
            val toAdd = patients
                .filter { it.nom.trim().uppercase() !in existingNames }
                .sortedBy { it.nom.trim().uppercase() }

            // Ajoute chaque nouveau patient à la première ligne vide
            var rowNum = sheet.lastRowNum + 1
            for (p in toAdd) {
                val row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(p.nom)
                row.createCell(1).setCellValue(p.dateNaissance)
                row.createCell(2).setCellValue(p.dep)
                row.createCell(3).setCellValue(p.da)
                row.createCell(4).setCellValue(p.validiteDaDu)
                row.createCell(5).setCellValue(p.validiteDaAu)
                row.createCell(6).setCellValue(p.transportDu)
                row.createCell(7).setCellValue(p.transportAu)
                row.createCell(8).setCellValue(p.adresseGeographique)
                row.createCell(9).setCellValue(p.trajet)
                row.createCell(10).setCellValue(p.pointPcArrivee)
                row.createCell(11).setCellValue(p.complementAdresse)
                row.createCell(12).setCellValue(p.tel)
                row.createCell(13).setCellValue(p.dn)
                row.createCell(14).setCellValue(p.pk)
                row.createCell(15).setCellValue(p.kmSuppl)
                row.createCell(16).setCellValue(p.pkCentreSoin)
                row.createCell(17).setCellValue(p.tf)
            }
            val out: OutputStream? = contentResolver.openOutputStream(fileUri)
            out.use { workbook.write(it) }
            workbook.close()
        } catch (e: Exception) {
            Log.e("ExcelUtil", "Erreur écriture Excel", e)
        }
    }

    // Pour obtenir le nom du fichier à partir de l'Uri (affichage dans l'UI)
    fun getFileName(context: Context, uri: Uri): String {
        var name: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
        return name ?: uri.lastPathSegment ?: "?"
    }

    // Diff simplifié (nom = clé)
    fun diffPatients(local: List<Patient>, excel: List<Patient>): List<Patient> {
        val localMap = local.associateBy { it.nom.trim().uppercase() }
        val excelMap = excel.associateBy { it.nom.trim().uppercase() }
        val modified = mutableListOf<Patient>()
        for ((nom, ex) in excelMap) {
            val loc = localMap[nom]
            if (loc == null || ex != loc) {
                modified.add(ex)
            }
        }
        return modified
    }

    private fun getCellString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(cell.dateCellValue)
                } else {
                    val d = cell.numericCellValue
                    if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
                }
            }
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> try {
                cell.stringCellValue
            } catch (e: Exception) {
                try {
                    val d = cell.numericCellValue
                    if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
                } catch (ex: Exception) {
                    ""
                }
            }
            else -> ""
        }
    }

    private fun getCellAsNumberOrString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.NUMERIC -> {
                val d = cell.numericCellValue
                if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
            }
            else -> getCellString(cell)
        }
    }

    private fun getCellAsDateOrString(cell: org.apache.poi.ss.usermodel.Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.NUMERIC -> {
                val date = if (DateUtil.isCellDateFormatted(cell)) {
                    cell.dateCellValue
                } else {
                    DateUtil.getJavaDate(cell.numericCellValue)
                }
                SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date)
            }
            CellType.STRING -> {
                val str = cell.stringCellValue.trim()
                try {
                    val asDouble = str.toDoubleOrNull()
                    if (asDouble != null) {
                        val date = DateUtil.getJavaDate(asDouble)
                        SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date)
                    } else str
                } catch (_: Exception) {
                    str
                }
            }
            else -> getCellString(cell)
        }
    }
}