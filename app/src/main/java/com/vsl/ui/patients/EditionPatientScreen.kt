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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import com.vsl.data.db.Patient
import com.vsl.ui.theme.WarningOrange
import com.vsl.ui.theme.ErrorRed
import com.vsl.utils.isValidDate
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditionPatientScreen(
    modifier: Modifier = Modifier,
    initialPatient: Patient,
    isNewPatient: Boolean = false,
    excelFileName: String = "",
    onSave: (Patient) -> Unit,
    onCancel: () -> Unit = {},
    onDelete: ((Patient) -> Unit)? = null,
    canDelete: Boolean = false
) {
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

    var errors by remember { mutableStateOf(emptyMap<String, String>()) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val tels = tel.split('\n')
    var telPrincipal by remember { mutableStateOf(tels.getOrNull(0)?.trim().orEmpty()) }
    var telAutre by remember { mutableStateOf(tels.getOrNull(1)?.trim().orEmpty()) }
    fun updateTelFields() {
        tel = if (telAutre.isNotEmpty()) "$telPrincipal\n$telAutre" else telPrincipal
    }

    val isDepWarning = dep.isBlank()
    val isDaAuWarning = try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val daAuDate = sdf.parse(validiteDaAu)
        val now = Calendar.getInstance().time
        daAuDate != null && daAuDate.before(now)
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
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // TITRE TOUJOURS VISIBLE
        Text(
            if (isNewPatient) "Nouveau patient" else "Edition patient",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Info secondaire (nom + fichier), limitée en hauteur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    isNewPatient -> "- Saisir identité"
                    nom.isNotBlank() -> "- $nom"
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (excelFileName.isNotBlank()) {
                Spacer(Modifier.width(8.dp))
                Text(
                    "- $excelFileName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Champ Nom, Tel, DN
        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom*") },
            modifier = Modifier.fillMaxWidth(),
            isError = errors.containsKey("nom"),
            singleLine = false,
            maxLines = 2, // Permet l'affichage sur 2 lignes
            textStyle = LocalTextStyle.current.copy(lineHeight = MaterialTheme.typography.bodyLarge.lineHeight)
        )
        if (errors["nom"] != null) Text(errors["nom"]!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(
                modifier = Modifier.weight(1f),
                label = "Date de naissance",
                value = dateNaissance,
                onValueChange = { dateNaissance = it },
                context = context,
                isError = errors.containsKey("dateNaissance"),
                error = errors["dateNaissance"]
            )
            OutlinedTextField(
                value = dn,
                onValueChange = { dn = it },
                modifier = Modifier.weight(1f),
                label = { Text("DN") },
                singleLine = true
            )
        }

        // Téléphones
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = telPrincipal,
                onValueChange = {
                    telPrincipal = it
                    updateTelFields()
                },
                modifier = Modifier.weight(1f),
                label = { Text("Téléphone") },
                singleLine = true
            )
            OutlinedTextField(
                value = telAutre,
                onValueChange = {
                    telAutre = it
                    updateTelFields()
                },
                modifier = Modifier.weight(1f),
                label = { Text("Autre") },
                singleLine = true
            )
        }

        // DEP / DA
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = dep,
                onValueChange = { dep = it },
                modifier = Modifier.weight(1f),
                label = {
                    if (isDepWarning)
                        Text("DEP", color = ErrorRed)
                    else
                        Text("DEP")
                },
                singleLine = true,
                isError = isDepWarning
            )
            OutlinedTextField(
                value = da,
                onValueChange = { da = it },
                modifier = Modifier.weight(1f),
                label = { Text("DA") },
                singleLine = true
            )
        }
        if (isDepWarning) {
            Text("Attention DEP", color = ErrorRed, style = MaterialTheme.typography.bodySmall)
        }

        // Validité DA
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
                label = "au",
                value = validiteDaAu,
                onValueChange = { validiteDaAu = it },
                context = context,
                isError = errors.containsKey("validiteDaAu") || isDaAuWarning,
                error = if (isDaAuWarning) "Date dépassée" else errors["validiteDaAu"],
                warning = isDaAuWarning
            )
        }

        // Transport
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
                label = "au",
                value = transportAu,
                onValueChange = { transportAu = it },
                context = context,
                isError = errors.containsKey("transportAu"),
                error = errors["transportAu"]
            )
        }

        OutlinedTextField(
            value = adresseGeographique,
            onValueChange = { adresseGeographique = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Adresse géographique") }
        )

        // Complément d'adresse seul
        OutlinedTextField(
            value = complementAdresse,
            onValueChange = { complementAdresse = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Complément d'adresse") }
        )

        // PK / Km suppl
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = pk,
                onValueChange = { pk = it },
                modifier = Modifier.weight(1f),
                label = { Text("PK") },
                singleLine = true
            )
            OutlinedTextField(
                value = kmSuppl,
                onValueChange = { kmSuppl = it },
                modifier = Modifier.weight(1f),
                label = { Text("Km suppl") },
                singleLine = true
            )
        }

        // Trajet / PK centre soin
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = trajet,
                onValueChange = { trajet = it },
                modifier = Modifier.weight(1f),
                label = { Text("Trajet") }
            )
            OutlinedTextField(
                value = pkCentreSoin,
                onValueChange = { pkCentreSoin = it },
                modifier = Modifier.weight(1f),
                label = { Text("PK centre soin") },
                singleLine = true
            )
        }

        // Code traitement (ex-TF)
        OutlinedTextField(
            value = tf,
            onValueChange = { tf = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Code traitement") },
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
                                isLocallyModified = true
                            )
                        )
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
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
                                isLocallyModified = true
                            )
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("Enregistrer") }
        }
    }
}

// --- DateField reste inchangé ---
@Composable
fun DateField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    context: Context,
    isError: Boolean = false,
    error: String? = null,
    warning: Boolean = false
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
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        label = { Text(label) },
        singleLine = true,
        readOnly = true,
        isError = isError || warning,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.DateRange, contentDescription = "Choisir une date")
            }
        },
        colors = if (warning) {
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WarningOrange,
                unfocusedBorderColor = WarningOrange,
                cursorColor = WarningOrange
            )
        } else {
            OutlinedTextFieldDefaults.colors()
        }
    )

    if (error != null) {
        Text(error, color = if (warning) WarningOrange else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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