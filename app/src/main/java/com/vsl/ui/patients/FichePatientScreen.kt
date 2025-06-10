@file:OptIn(ExperimentalMaterial3Api::class)

package com.vsl.ui.patients

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.vsl.data.db.Patient
import com.vsl.ui.theme.ErrorRed
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FichePatientScreen(
    patients: List<Patient>,
    excelFileName: String = "",
    onAddPatient: () -> Unit,
    onEditPatient: (Patient) -> Unit,
    onImportRequest: () -> Unit = {},
    onExportRequest: () -> Unit = {},
) {
    val noms = remember(patients) { patients.map { it.nom }.distinct().sorted() }
    var selectedNom by remember { mutableStateOf(noms.firstOrNull() ?: "") }
    val selectedPatient = patients.lastOrNull { it.nom == selectedNom }
    val scrollState = rememberScrollState()

    var showSyncMenu by remember { mutableStateOf(false) }
    var showConfirmImport by remember { mutableStateOf(false) }
    var showConfirmExport by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPatient) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Titre + Sync + nom du fichier source
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Fiche patient",
                    style = MaterialTheme.typography.titleLarge,
                )
                if (excelFileName.isNotBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = excelFileName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.weight(1f))
                // Bouton Sync (menu Import/Export)
                Box {
                    IconButton(onClick = { showSyncMenu = true }) {
                        Icon(Icons.Filled.Sync, contentDescription = "Synchroniser", tint = MaterialTheme.colorScheme.primary)
                    }
                    DropdownMenu(
                        expanded = showSyncMenu,
                        onDismissRequest = { showSyncMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Import") },
                            onClick = {
                                showSyncMenu = false
                                showConfirmImport = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export") },
                            onClick = {
                                showSyncMenu = false
                                showConfirmExport = true
                            }
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 40.dp)) {
                        Text("Sync", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Sélecteur/autocomplete du patient + crayon (ROW bien placé ici)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NomPatientAutoComplete(
                    allPatientNames = noms,
                    value = selectedNom,
                    onValueChange = { selectedNom = it },
                    label = "Nom du patient",
                    modifier = Modifier.weight(1f)
                )
                if (selectedPatient != null) {
                    IconButton(
                        modifier = Modifier.size(20.dp),
                        onClick = { onEditPatient(selectedPatient) }
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editer", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (selectedPatient != null) {
                PatientFieldsGroupedCard(selectedPatient)
            } else {
                Text(
                    "Sélectionnez un patient.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // Dialog de confirmation Import
        if (showConfirmImport) {
            AlertDialog(
                onDismissRequest = { showConfirmImport = false },
                title = { Text("Confirmer l'import Excel") },
                text = { Text("Cette opération va remplacer la liste actuelle par celle du fichier Excel. Continuer ?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmImport = false
                            onImportRequest()
                        }
                    ) { Text("Importer") }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmImport = false }) { Text("Annuler") }
                }
            )
        }
        // Dialog de confirmation Export
        if (showConfirmExport) {
            AlertDialog(
                onDismissRequest = { showConfirmExport = false },
                title = { Text("Confirmer l'export Excel") },
                text = { Text("Exporter la liste des patients vers le fichier Excel ?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmExport = false
                            onExportRequest()
                        }
                    ) { Text("Exporter") }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmExport = false }) { Text("Annuler") }
                }
            )
        }
    }
}

// ... PatientFieldsGroupedCard, ReadOnlyField, excelDateToString restent inchangés ...

@Composable
fun PatientFieldsGroupedCard(patient: Patient) {
    val depWarning = patient.dep.isBlank()
    val depColor = if (depWarning) ErrorRed else Color.Unspecified

    val validiteDaAuStr = excelDateToString(patient.validiteDaAu)
    val isDaAuWarning = try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val daAuDate = sdf.parse(validiteDaAuStr)
        val now = Calendar.getInstance().time
        daAuDate != null && daAuDate.before(now)
    } catch (_: Exception) { false }
    val daColor = if (isDaAuWarning) ErrorRed else Color.Unspecified

    val dateNaissanceStr = excelDateToString(patient.dateNaissance)
    val validiteDaDuStr = excelDateToString(patient.validiteDaDu)
    val transportDuStr = excelDateToString(patient.transportDu)
    val transportAuStr = excelDateToString(patient.transportAu)

    // Style commun
    val cardShape: Shape = RoundedCornerShape(18.dp)

    // Bloc 1 : Date de naissance + DN + DEP
    OutlinedCard(
        shape = cardShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReadOnlyField("Date de naissance", dateNaissanceStr, Modifier.weight(1f))
            ReadOnlyField("DN", patient.dn, Modifier.weight(1f))
            ReadOnlyField("DEP", patient.dep, Modifier.weight(1f), color = depColor)
        }
        if (depWarning) {
            Text(
                "Attention DEP",
                color = ErrorRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
    Spacer(Modifier.height(12.dp))

    // Bloc 2 : DA + Validité DA du-au + Transport
    OutlinedCard(
        shape = cardShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(10.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReadOnlyField("DA", patient.da, Modifier.weight(1f))
                ReadOnlyField(
                    "Validité DA",
                    "du $validiteDaDuStr au $validiteDaAuStr",
                    Modifier.weight(2f),
                    color = daColor
                )
            }
            if (isDaAuWarning) {
                Text(
                    "Validité DA limite ou dépassée",
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReadOnlyField("Transport", "du $transportDuStr au $transportAuStr", Modifier.weight(1f))
            }
        }
    }
    Spacer(Modifier.height(12.dp))

    // Bloc 3 : Adresse géographique + Complément + PK + Km suppl
    OutlinedCard(
        shape = cardShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(10.dp)) {
            ReadOnlyField("Adresse géographique", patient.adresseGeographique, Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReadOnlyField("Complément d'adresse", patient.complementAdresse, Modifier.weight(2f))
                ReadOnlyField("PK", patient.pk, Modifier.weight(1f))
                ReadOnlyField("Km suppl", patient.kmSuppl, Modifier.weight(1f))
            }
        }
    }
    Spacer(Modifier.height(12.dp))

    // Bloc 4 : Point PC arrivé + Trajet + PK centre soin
    OutlinedCard(
        shape = cardShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReadOnlyField("Point PC arrivé", patient.pointPcArrivee, Modifier.weight(1f))
            ReadOnlyField("Trajet", patient.trajet, Modifier.weight(1f))
            ReadOnlyField("PK centre soin", patient.pkCentreSoin, Modifier.weight(1f))
        }
    }
    Spacer(Modifier.height(12.dp))

    // Bloc 5 : Code traitement seul
    OutlinedCard(
        shape = cardShape,
        modifier = Modifier.fillMaxWidth()
    ) {
        ReadOnlyField(
            "Code traitement", patient.tf,
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}

@Composable
fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    Column(modifier.padding(vertical = 2.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onBackground
        )
    }
}

// Utilitaire pour convertir un nombre Excel (date) en chaîne JJ/MM/AAAA
fun excelDateToString(value: String): String {
    if (value.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) return value
    return try {
        val days = value.toDouble().toInt()
        val ms = (days - 25569) * 86400 * 1000L
        val date = Date(ms)
        SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(date)
    } catch (e: Exception) {
        value
    }
}