package com.vsl.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Vérifie si la chaîne est une date valide au format JJ/MM/AAAA (dd/MM/yyyy).
 */
fun String.isValidDate(): Boolean {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).apply { isLenient = false }.parse(this)
        true
    } catch (_: Exception) {
        false
    }
}