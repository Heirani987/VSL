package com.vsl.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.excelSourceDataStore: DataStore<Preferences> by preferencesDataStore(name = "excel_source")

object ExcelSourceManager {
    // Utilisation de stringPreferencesKey à la place de preferencesKey
    private val EXCEL_SOURCE_KEY = stringPreferencesKey("excel_source")

    suspend fun saveExcelSource(context: Context, source: String) {
        context.excelSourceDataStore.edit { preferences ->
            preferences[EXCEL_SOURCE_KEY] = source
        }
    }

    fun getExcelSource(context: Context): Flow<String?> {
        return context.excelSourceDataStore.data.map { preferences ->
            preferences[EXCEL_SOURCE_KEY]
        }
    }
}