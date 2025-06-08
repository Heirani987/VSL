package com.vsl

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.vsl.data.db.AppDatabase
import com.vsl.data.db.PatientRepository
import com.vsl.ui.PatientScreen
import com.vsl.ui.PatientViewModel
import com.vsl.ui.PatientViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PatientViewModel

    // Lanceur du sélecteur de fichier Excel
    private val pickExcelFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            // Appelle le ViewModel pour importer les patients depuis Excel
            viewModel.importPatientsFromExcel(contentResolver, it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = PatientRepository(database.patientDao())
        val viewModelFactory = PatientViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PatientViewModel::class.java]

        setContent {
            PatientScreen(
                viewModel = viewModel,
                onImportExcel = {
                    pickExcelFile.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                }
            )
        }
    }
}