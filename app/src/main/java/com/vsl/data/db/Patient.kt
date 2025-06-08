package com.vsl.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
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
    val tf: String = ""
)