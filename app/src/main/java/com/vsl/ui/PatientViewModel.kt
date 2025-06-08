package com.vsl.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsl.data.db.Patient
import com.vsl.data.db.PatientRepository
import com.vsl.util.ExcelUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    val patients: StateFlow<List<Patient>> = repository.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(patient: Patient) {
        viewModelScope.launch {
            repository.insert(patient)
        }
    }

    fun delete(patient: Patient) {
        viewModelScope.launch {
            repository.delete(patient)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    // Nouvelle méthode pour importer les patients depuis Excel
    fun importPatientsFromExcel(contentResolver: ContentResolver, fileUri: Uri) {
        viewModelScope.launch {
            val imported = ExcelUtil.readPatientsFromPlanningSheet(contentResolver, fileUri)
            imported.forEach { repository.insert(it) }
        }
    }
}