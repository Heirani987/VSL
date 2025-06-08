@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.vsl.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.vsl.data.db.Patient

@Composable
fun PatientScreen(
    viewModel: PatientViewModel,
    onImportExcel: () -> Unit
) {
    val patients by viewModel.uniquePatients.collectAsState()
    val importResult by viewModel.importResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Affichage du Snackbar après import
    LaunchedEffect(importResult) {
        when (importResult) {
            true -> {
                snackbarHostState.showSnackbar("Import Excel réussi 🎉")
                viewModel.clearImportResult()
            }
            false -> {
                snackbarHostState.showSnackbar("Échec de l'import Excel")
                viewModel.clearImportResult()
            }
            null -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
            Text(
                text = "Liste des patients",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = onImportExcel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Importer Excel")
            }

            // Formulaire d'ajout de patient (garde ta logique existante ici)
            PatientForm(
                onSubmit = { patient -> viewModel.insert(patient) },
                existingPatientNames = patients.map { it.nom },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (patients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun patient à afficher.")
                }
            } else {
                LazyColumn {
                    items(patients) { patient ->
                        PatientListItem(
                            patient = patient,
                            onLongClick = { viewModel.delete(patient) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientListItem(
    patient: Patient,
    onLongClick: (Patient) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {}, // Action simple clic (facultatif)
                onLongClick = { onLongClick(patient) }
            )
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = patient.nom,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "ID: ${patient.id}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}