package com.vsl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsl.data.db.Patient

@Composable
fun PatientForm(
    onSubmit: (Patient) -> Unit,
    modifier: Modifier = Modifier
) {
    var nom by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var dep by remember { mutableStateOf("") }
    var da by remember { mutableStateOf("") }
    var validiteDaDu by remember { mutableStateOf("") }
    var validiteDaAu by remember { mutableStateOf("") }
    var transportDu by remember { mutableStateOf("") }
    var transportAu by remember { mutableStateOf("") }
    var adresseGeographique by remember { mutableStateOf("") }
    var trajet by remember { mutableStateOf("") }
    var pointPcArrivee by remember { mutableStateOf("") }
    var complementAdresse by remember { mutableStateOf("") }
    var tel by remember { mutableStateOf("") }
    var dn by remember { mutableStateOf("") }
    var pk by remember { mutableStateOf("") }
    var kmSuppl by remember { mutableStateOf("") }
    var pkCentreSoin by remember { mutableStateOf("") }
    var tf by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dateNaissance, onValueChange = { dateNaissance = it }, label = { Text("Date de naissance") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dep, onValueChange = { dep = it }, label = { Text("Département") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = da, onValueChange = { da = it }, label = { Text("DA") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = validiteDaDu, onValueChange = { validiteDaDu = it }, label = { Text("Validité DA du") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = validiteDaAu, onValueChange = { validiteDaAu = it }, label = { Text("Validité DA au") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = transportDu, onValueChange = { transportDu = it }, label = { Text("Transport du") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = transportAu, onValueChange = { transportAu = it }, label = { Text("Transport au") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = adresseGeographique, onValueChange = { adresseGeographique = it }, label = { Text("Adresse géographique") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = trajet, onValueChange = { trajet = it }, label = { Text("Trajet") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pointPcArrivee, onValueChange = { pointPcArrivee = it }, label = { Text("Point PC arrivée") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = complementAdresse, onValueChange = { complementAdresse = it }, label = { Text("Complément d'adresse") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tel, onValueChange = { tel = it }, label = { Text("Téléphone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = dn, onValueChange = { dn = it }, label = { Text("DN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pk, onValueChange = { pk = it }, label = { Text("PK") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = kmSuppl, onValueChange = { kmSuppl = it }, label = { Text("Km supplémentaire") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pkCentreSoin, onValueChange = { pkCentreSoin = it }, label = { Text("PK centre de soin") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tf, onValueChange = { tf = it }, label = { Text("TF") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                onSubmit(
                    Patient(
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
                        tf = tf
                    )
                )
                // Réinitialise le formulaire si besoin
                nom = ""; dateNaissance = ""; dep = ""; da = ""; validiteDaDu = ""; validiteDaAu = ""
                transportDu = ""; transportAu = ""; adresseGeographique = ""; trajet = ""; pointPcArrivee = ""; complementAdresse = ""
                tel = ""; dn = ""; pk = ""; kmSuppl = ""; pkCentreSoin = ""; tf = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }
}