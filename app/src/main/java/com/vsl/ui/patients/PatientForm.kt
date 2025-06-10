@file:OptIn(ExperimentalMaterial3Api::class)

package com.vsl.ui.patients

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vsl.data.db.Patient
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.Alignment

@Composable
fun PatientFormScreen(
    modifier: Modifier = Modifier,
    initialPatient: Patient = Patient(
        nom = "",
        dateNaissance = "",
        dep = "",
        da = "",
        validiteDaDu = "",
        validiteDaAu = "",
        transportDu = "",
        transportAu = "",
        adresseGeographique = "",
        trajet = "",
        pointPcArrivee = "",
        complementAdresse = "",
        tel = "",
        dn = "",
        pk = "",
        kmSuppl = "",
        pkCentreSoin = "",
        tf = "",
        isLocallyModified = true
    ),
    patients: List<Patient> = emptyList(),
    existingPatientNames: List<String> = emptyList(),
    excelFileName: String = "",
    onSave: (Patient) -> Unit,
    onCancel: () -> Unit = {},
    onDelete: ((Patient) -> Unit)? = null,
    canDelete: Boolean = false,
) {
    // Champs contrôlés
    var nom by remember { mutableStateOf(initialPatient.nom) }
    var dateNaissance by remember { mutableStateOf(initialPatient.dateNaissance) }
    var dep by remember { mutableStateOf(initialPatient.dep) }
    var da by remember { mutableStateOf(initialPatient.da) }
    var validiteDaDu by remember { mutableStateOf(initialPatient.validiteDaDu) }
    var validiteDaAu by remember { mutableStateOf(initialPatient.validiteDaAu) }
    var transportDu by remember { mutableStateOf(initialPatient.transportDu) }
    var transportAu by remember { mutableStateOf(initialPatient.transportAu) }
    var adresseGeographique by remember { mutableStateOf(initialPatient.adresseGeographique) }
    var trajet by remember { mutableStateOf(initialPatient.trajet) }
    var pointPcArrivee by remember { mutableStateOf(initialPatient.pointPcArrivee) }
    var complementAdresse by remember { mutableStateOf(initialPatient.complementAdresse) }
    var tel by remember { mutableStateOf(initialPatient.tel) }
    var dn by remember { mutableStateOf(initialPatient.dn) }
    var pk by remember { mutableStateOf(initialPatient.pk) }
    var kmSuppl by remember { mutableStateOf(initialPatient.kmSuppl) }
    var pkCentreSoin by remember { mutableStateOf(initialPatient.pkCentreSoin) }
    var tf by remember { mutableStateOf(initialPatient.tf) }

    var expanded by remember { mutableStateOf(false) }

    // Validation
    var errors by remember { mutableStateOf(emptyMap<String, String>()) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Auto-complétion noms
    val filteredNames = remember(nom, existingPatientNames) {
        existingPatientNames.filter { it.contains(nom, ignoreCase = true) && it.isNotBlank() }.distinct()
    }

    // Remplissage automatique des champs lors de la sélection d'un nom
    LaunchedEffect(nom) {
        if (nom.isNotBlank() && patients.any { it.nom.trim().equals(nom.trim(), ignoreCase = true) }) {
            val patient = patients.lastOrNull { it.nom.trim().equals(nom.trim(), ignoreCase = true) }
            if (patient != null) {
                if (patient.dateNaissance != dateNaissance) dateNaissance = patient.dateNaissance
                if (patient.dep != dep) dep = patient.dep
                if (patient.da != da) da = patient.da
                if (patient.validiteDaDu != validiteDaDu) validiteDaDu = patient.validiteDaDu
                if (patient.validiteDaAu != validiteDaAu) validiteDaAu = patient.validiteDaAu
                if (patient.transportDu != transportDu) transportDu = patient.transportDu
                if (patient.transportAu != transportAu) transportAu = patient.transportAu
                if (patient.adresseGeographique != adresseGeographique) adresseGeographique = patient.adresseGeographique
                if (patient.trajet != trajet) trajet = patient.trajet
                if (patient.pointPcArrivee != pointPcArrivee) pointPcArrivee = patient.pointPcArrivee
                if (patient.complementAdresse != complementAdresse) complementAdresse = patient.complementAdresse
                if (patient.tel != tel) tel = patient.tel
                if (patient.dn != dn) dn = patient.dn
                if (patient.pk != pk) pk = patient.pk
                if (patient.kmSuppl != kmSuppl) kmSuppl = patient.kmSuppl
                if (patient.pkCentreSoin != pkCentreSoin) pkCentreSoin = patient.pkCentreSoin
                if (patient.tf != tf) tf = patient.tf
            }
        }
    }

    // Calcul du warning validite DA au (< 15j)
    val isDaAuWarning = try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val daAuDate = sdf.parse(validiteDaAu)
        val now = Calendar.getInstance().time
        if (daAuDate != null) {
            val diff = (daAuDate.time - now.time) / (1000 * 60 * 60 * 24)
            diff in 0..15
        } else {
            false
        }
    } catch (_: Exception) { false }

    fun validate(): Boolean {
        val errs = mutableMapOf<String, String>()
        if (nom.trim().isEmpty()) errs["nom"] = "Le nom est obligatoire"
        if (dateNaissance.isNotBlank() && !dateNaissance.isValidDate()) errs["dateNaissance"] = "Format: JJ/MM/AAAA"
        if (validiteDaDu.isNotBlank() && !validiteDaDu.isValidDate()) errs["validiteDaDu"] = "Format: JJ/MM/AAAA"
        if (validiteDaAu.isNotBlank() && !validiteDaAu.isValidDate()) errs["validiteDaAu"] = "Format: JJ/MM/AAAA"
        if (transportDu.isNotBlank() && !transportDu.isValidDate()) errs["transportDu"] = "Format: JJ/MM/AAAA"
        if (transportAu.isNotBlank() && !transportAu.isValidDate()) errs["transportAu"] = "Format: JJ/MM/AAAA"
        errors = errs
        return errs.isEmpty()
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Titre + Nom du fichier source
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Fiche patient",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (excelFileName.isNotBlank()) {
                Spacer(Modifier.width(10.dp))
                Text(
                    "- $excelFileName",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Champ "Nom" avec autocomplétion
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = nom,
                onValueChange = {
                    nom = it
                    expanded = it.isNotBlank() && filteredNames.isNotEmpty()
                },
                label = { Text("Nom*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                singleLine = true,
                isError = errors.containsKey("nom"),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded && filteredNames.isNotEmpty(),
                onDismissRequest = { expanded = false }
            ) {
                filteredNames.forEach { name ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            nom = name
                            expanded = false
                        }
                    )
                }
            }
        }
        if (errors["nom"] != null) Text(errors["nom"]!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)

        // Date de naissance, Téléphone et DN sur une ligne (date au-dessus)
        Column {
            DateField(
                modifier = Modifier.fillMaxWidth(),
                label = "Date de naissance",
                value = dateNaissance,
                onValueChange = { dateNaissance = it },
                context = context,
                isError = errors.containsKey("dateNaissance"),
                error = errors["dateNaissance"]
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tel,
                    onValueChange = { tel = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Téléphone") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = dn,
                    onValueChange = { dn = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("DN") },
                    singleLine = true
                )
            }
        }

        // DEP et DA côte à côte
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = dep,
                onValueChange = { dep = it },
                modifier = Modifier.weight(1f),
                label = {
                    if (dep.isBlank())
                        Text("Attention DEP", color = Color.Red)
                    else
                        Text("DEP")
                },
                singleLine = true,
                isError = dep.isBlank()
            )
            OutlinedTextField(
                value = da,
                onValueChange = { da = it },
                modifier = Modifier.weight(1f),
                label = { Text("DA") },
                singleLine = true
            )
        }

        // Validité DA du/au côte à côte
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(
                modifier = Modifier.weight(1f),
                label = "Validité DA du",
                value = validiteDaDu,
                onValueChange = { validiteDaDu = it },
                context = context,
                isError = errors.containsKey("validiteDaDu"),
                error = errors["validiteDaDu"]
            )
            DateField(
                modifier = Modifier.weight(1f),
                label = "Validité DA au",
                value = validiteDaAu,
                onValueChange = { validiteDaAu = it },
                context = context,
                isError = errors.containsKey("validiteDaAu") || isDaAuWarning,
                error = if (isDaAuWarning) "Date bientôt expirée" else errors["validiteDaAu"]
            )
        }

        // Transport du/au côte à côte
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(
                modifier = Modifier.weight(1f),
                label = "Transport du",
                value = transportDu,
                onValueChange = { transportDu = it },
                context = context,
                isError = errors.containsKey("transportDu"),
                error = errors["transportDu"]
            )
            DateField(
                modifier = Modifier.weight(1f),
                label = "Transport au",
                value = transportAu,
                onValueChange = { transportAu = it },
                context = context,
                isError = errors.containsKey("transportAu"),
                error = errors["transportAu"]
            )
        }

        // Champs restants sur une colonne
        OutlinedTextField(
            value = adresseGeographique,
            onValueChange = { adresseGeographique = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Adresse géographique") }
        )
        OutlinedTextField(
            value = trajet,
            onValueChange = { trajet = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Trajet") }
        )
        OutlinedTextField(
            value = pointPcArrivee,
            onValueChange = { pointPcArrivee = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Point PC arrivée") }
        )
        OutlinedTextField(
            value = complementAdresse,
            onValueChange = { complementAdresse = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Complément adresse") }
        )
        OutlinedTextField(
            value = pk,
            onValueChange = { pk = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("PK") },
            singleLine = true
        )
        OutlinedTextField(
            value = kmSuppl,
            onValueChange = { kmSuppl = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Km suppl") },
            singleLine = true
        )
        OutlinedTextField(
            value = pkCentreSoin,
            onValueChange = { pkCentreSoin = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("PK centre soin") },
            singleLine = true
        )
        OutlinedTextField(
            value = tf,
            onValueChange = { tf = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("TF") },
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (canDelete && onDelete != null) {
                OutlinedButton(
                    onClick = {
                        onDelete(
                            initialPatient.copy(
                                nom = nom,
                                dateNaissance = dateNaissance,
                                dep = dep,
                                da = da,
                                validiteDaDu = validiteDaDu,
                                validiteDaAu = validiteDaAu,
                                transportDu = transportDu,
                                transportAu = transportAu,
                                adresseGeographique = adresseGeographique,
                                trajet = trajet,
                                pointPcArrivee = pointPcArrivee,
                                complementAdresse = complementAdresse,
                                tel = tel,
                                dn = dn,
                                pk = pk,
                                kmSuppl = kmSuppl,
                                pkCentreSoin = pkCentreSoin,
                                tf = tf,
                                isLocallyModified = initialPatient.isLocallyModified
                            )
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) { Text("Supprimer") }
                Spacer(Modifier.width(16.dp))
            }
            OutlinedButton(
                onClick = { onCancel() },
                modifier = Modifier.weight(1f)
            ) { Text("Annuler") }
            Spacer(Modifier.width(16.dp))
            Button(
                onClick = {
                    if (validate()) {
                        onSave(
                            initialPatient.copy(
                                nom = nom,
                                dateNaissance = dateNaissance,
                                dep = dep,
                                da = da,
                                validiteDaDu = validiteDaDu,
                                validiteDaAu = validiteDaAu,
                                transportDu = transportDu,
                                transportAu = transportAu,
                                adresseGeographique = adresseGeographique,
                                trajet = trajet,
                                pointPcArrivee = pointPcArrivee,
                                complementAdresse = complementAdresse,
                                tel = tel,
                                dn = dn,
                                pk = pk,
                                kmSuppl = kmSuppl,
                                pkCentreSoin = pkCentreSoin,
                                tf = tf,
                                isLocallyModified = initialPatient.isLocallyModified
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Enregistrer") }
        }
    }
}

@Composable
fun DateField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    context: Context,
    isError: Boolean = false,
    error: String? = null
) {
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }
    var showDialog by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(value) {
        try {
            if (value.isNotBlank()) {
                val date = sdf.parse(value)
                date?.let { calendar.time = it }
            }
        } catch (_: Exception) {}
    }

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        label = { Text(label) },
        singleLine = true,
        readOnly = true,
        isError = isError,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Choisir une date")
            }
        }
    )

    if (error != null) {
        Text(error, color = Color.Red, style = MaterialTheme.typography.bodySmall)
    }

    if (showDialog) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                onValueChange(sdf.format(cal.time))
                showDialog = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply { setOnDismissListener { showDialog = false } }
            .show()
    }
}

// Extension de validation de date au format dd/MM/yyyy
fun String.isValidDate(): Boolean {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).apply { isLenient = false }.parse(this)
        true
    } catch (_: Exception) {
        false
    }
}