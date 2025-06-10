package com.vsl.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val nom: String = "",
    val dateNaissance: String = "",
    val dep: String = "",
    val da: String = "",
    val validiteDaDu: String = "",
    val validiteDaAu: String = "",
    val transportDu: String = "",
    val transportAu: String = "",
    val adresseGeographique: String = "",
    val trajet: String = "",
    val pointPcArrivee: String = "",
    val complementAdresse: String = "",
    val tel: String = "",
    val dn: String = "",
    val pk: String = "",
    val kmSuppl: String = "",
    val pkCentreSoin: String = "",
    val tf: String = "",
    val isLocallyModified: Boolean = false // true = non exporté, false = exporté
)

/**
 * Fournit une instance de Patient vide pour les écrans de création.
 */
fun emptyPatient() = Patient()