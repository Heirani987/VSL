package com.vsl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vsl.data.db.AppDatabase
import com.vsl.data.db.PatientRepository
import com.vsl.ui.MainApp
import com.vsl.ui.patients.PatientViewModel
import com.vsl.ui.patients.PatientViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instancie la database, le DAO et le repository
        val database = AppDatabase.getDatabase(applicationContext)
        val patientDao = database.patientDao()
        val repository = PatientRepository(patientDao, applicationContext)

        setContent {
            // Passe la factory personnalisée à viewModel()
            val patientViewModel: PatientViewModel = viewModel(
                factory = PatientViewModelFactory(repository)
            )
            MainApp(patientViewModel = patientViewModel) // <--- Remplace PatientScreen par MainApp
        }
    }
}