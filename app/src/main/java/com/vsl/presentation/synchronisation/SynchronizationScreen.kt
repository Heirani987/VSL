package com.vsl.presentation.synchronisation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SynchronizationScreen(
    onImportClicked: () -> Unit,
    isLoading: Boolean,
    importSuccess: Boolean?,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onImportClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Importer un fichier Excel")
        }
        Spacer(Modifier.height(24.dp))
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            importSuccess == true -> {
                Text("Import réussi !", color = MaterialTheme.colorScheme.primary)
            }
            importSuccess == false && errorMessage != null -> {
                Text("Erreur import : $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}