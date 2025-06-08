package com.vsl.utils

import android.content.ContentResolver
import android.net.Uri
import com.vsl.data.db.Patient
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

object ExcelUtil {
    fun readPatientsFromPlanningSheet(contentResolver: ContentResolver, fileUri: Uri): List<Patient> {
        val patients = mutableListOf<Patient>()
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
                    // Si la cellule nom (C) est vide, on considère que la ligne est vide
                    val nom = getCellString(row.getCell(2)).trim()
                    if (nom.isEmpty()) continue
                    val patient = Patient(
                        nom = nom,
                        dateNaissance = getCellAsDateOrString(row.getCell(3), sdf),
                        dep = getCellString(row.getCell(4)),
                        da = getCellString(row.getCell(5)),
                        validiteDaDu = getCellAsDateOrString(row.getCell(6), sdf),
                        validiteDaAu = getCellAsDateOrString(row.getCell(7), sdf),
                        transportDu = getCellAsDateOrString(row.getCell(8), sdf),
                        transportAu = getCellAsDateOrString(row.getCell(9), sdf),
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

    private fun getCellAsDateOrString(cell: org.apache.poi.ss.usermodel.Cell?, sdf: SimpleDateFormat): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    sdf.format(cell.dateCellValue)
                } else {
                    val d = cell.numericCellValue
                    if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()
                }
            }
            CellType.STRING -> cell.stringCellValue
            else -> getCellString(cell)
        }
    }
}