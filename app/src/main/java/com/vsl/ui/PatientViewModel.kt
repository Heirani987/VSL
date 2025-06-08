package com.vsl.ui

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsl.data.db.Patient
import com.vsl.data.db.PatientRepository
import com.vsl.utils.ExcelUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    val patients: StateFlow<List<Patient>> = repository.getAllPatients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtrage du dernier noms de patients uniques
    val uniquePatients: StateFlow<List<Patient>> = patients
        .map { list ->
            list.groupBy { it.nom.trim().uppercase() }
                .map { (_, patientsWithSameName) -> patientsWithSameName.last() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Permet d’afficher un message de retour après import
    private val _importResult = MutableStateFlow<Boolean?>(null)
    val importResult: StateFlow<Boolean?> = _importResult.asStateFlow()

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

    fun importPatientsFromExcel(contentResolver: ContentResolver, fileUri: Uri) {
        viewModelScope.launch {
            try {
                val imported = ExcelUtil.readPatientsFromPlanningSheet(contentResolver, fileUri)
                if (imported.isNotEmpty()) {
                    repository.insertAll(imported) // insertAll (en masse) recommandé
                    _importResult.value = true
                } else {
                    _importResult.value = false
                }
            } catch (e: Exception) {
                _importResult.value = false
            }
        }
    }

    fun clearImportResult() {
        _importResult.value = null
    }
}