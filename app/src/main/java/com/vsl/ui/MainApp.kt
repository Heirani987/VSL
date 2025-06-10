package com.vsl.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vsl.data.db.emptyPatient
import com.vsl.data.db.Patient
import com.vsl.ui.patients.EditionPatientScreen
import com.vsl.ui.patients.PatientScreen
import com.vsl.ui.patients.PatientViewModel

@Composable
fun MainApp(
    patientViewModel: PatientViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "fichePatient") {
        composable("fichePatient") {
            PatientScreen(
                viewModel = patientViewModel,
                onAddPatient = {
                    navController.navigate("editionPatient")
                },
                onEditPatient = { patientId ->
                    navController.navigate("editionPatient/$patientId")
                }
            )
        }
        composable(
            "editionPatient/{patientId}",
            arguments = listOf(navArgument("patientId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            // Récupère l'état actuel de la liste des patients
            val patients = patientViewModel.uniquePatients.collectAsState().value
            val excelFileName = patientViewModel.excelFileName.collectAsState().value ?: ""
            // Extract patientId from the arguments
            val patientId = backStackEntry.arguments?.getLong("patientId") ?: -1L
            val selectedPatient: Patient? = patients.find { it.id == patientId }

            EditionPatientScreen(
                initialPatient = selectedPatient ?: emptyPatient(),
                isNewPatient = selectedPatient == null,
                excelFileName = excelFileName,
                onSave = { patient ->
                    patientViewModel.insert(patient)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
                onDelete = { patient ->
                    patientViewModel.delete(patient)
                    navController.popBackStack()
                },
                canDelete = selectedPatient != null
            )
        }
        composable("editionPatient") {
            // Mode création
            val excelFileName = patientViewModel.excelFileName.collectAsState().value ?: ""
            EditionPatientScreen(
                initialPatient = emptyPatient(),
                isNewPatient = true,
                excelFileName = excelFileName,
                onSave = { patient ->
                    patientViewModel.insert(patient)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() },
                canDelete = false
            )
        }
    }
}