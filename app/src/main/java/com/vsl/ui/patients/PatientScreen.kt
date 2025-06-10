package com.vsl.ui.patients

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun PatientScreen(
    viewModel: PatientViewModel,
    onAddPatient: () -> Unit,
    onEditPatient: (Long) -> Unit
) {
    val context = LocalContext.current
    val patients by viewModel.uniquePatients.collectAsState()
    val excelFileName by viewModel.excelFileName.collectAsState()
    val isExcelLoaded by viewModel.isExcelLoaded.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsState()

    // Import file picker
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importExcelFile(context, uri)
        }
    }
    // Export file picker
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.exportExcelFile(context, uri)
        }
    }

    // SnackBar
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            FichePatientScreen(
                patients = patients,
                excelFileName = excelFileName ?: "",
                onAddPatient = onAddPatient,
                onEditPatient = { patient -> onEditPatient(patient.id) },
                onImportRequest = {
                    importLauncher.launch(
                        arrayOf(
                            "application/vnd.ms-excel",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                    )
                },
                onExportRequest = {
                    exportLauncher.launch("patients_export.xlsx")
                }
            )
        }
    }
}