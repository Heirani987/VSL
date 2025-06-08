package com.vsl.util

import android.content.ContentResolver
import android.net.Uri
import com.vsl.data.db.Patient
import org.apache.poi.ss.usermodel.WorkbookFactory

object ExcelUtil {
    fun readPatientsFromPlanningSheet(contentResolver: ContentResolver, fileUri: Uri): List<Patient> {
        val patients = mutableListOf<Patient>()
        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheet("planning") ?: return emptyList()
            for (row in sheet.drop(6)) { // Commence à la ligne 7 (index 6)
                val nom = row.getCell(2)?.stringCellValue ?: ""
                // ...ajoute les autres champs selon ta classe Patient...
                if (nom.isNotBlank()) {
                    patients.add(
                        Patient(
                            nom = nom,
                            // ...autres champs...
                            id = 0 // id Room auto
                        )
                    )
                }
            }
            workbook.close()
        }
        return patients
    }
}