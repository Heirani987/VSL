package com.vsl.ui.patients

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsl.data.db.Patient
import com.vsl.data.db.PatientRepository
import com.vsl.utils.ExcelUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: PatientRepository
) : ViewModel() {

    // État de la liste de patients (exposée)
    private val _uniquePatients = MutableStateFlow<List<Patient>>(emptyList())
    val uniquePatients: StateFlow<List<Patient>> = _uniquePatients.asStateFlow()

    private val _excelFileName = MutableStateFlow<String?>(null)
    val excelFileName: StateFlow<String?> = _excelFileName.asStateFlow()

    private val _isExcelLoaded = MutableStateFlow(false)
    val isExcelLoaded: StateFlow<Boolean> = _isExcelLoaded.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        // Collecte le Flow du repository
        viewModelScope.launch {
            repository.getAllPatients().collectLatest { list ->
                _uniquePatients.value = list
            }
        }
    }

    // --------- CRUD Patients ---------
    fun insert(patient: Patient) {
        viewModelScope.launch {
            if (patient.id == 0L) {
                repository.insert(patient.copy(isLocallyModified = true))
            } else {
                repository.update(patient.copy(isLocallyModified = true))
            }
        }
    }

    fun delete(patient: Patient) {
        viewModelScope.launch {
            repository.delete(patient)
        }
    }

    fun updatePatients(patients: List<Patient>) {
        viewModelScope.launch {
            repository.deleteAll()
            patients.forEach { repository.insert(it) }
        }
    }

    fun clearPatients() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    /**
     * Import des patients depuis un fichier Excel sélectionné par l'utilisateur (Uri externe)
     */
    fun importExcelFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val importedPatients = ExcelUtil.readPatientsFromExcel(context, uri)
                repository.deleteAll()
                importedPatients.forEach { repository.insert(it) }
                _excelFileName.value = ExcelUtil.getFileName(context, uri)
                _isExcelLoaded.value = true
                _message.value = "Fichier importé avec succès"
            } catch (e: Exception) {
                _message.value = "Erreur lors de l'import : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Export des patients vers un fichier Excel sélectionné par l'utilisateur (Uri externe)
     */
    fun exportExcelFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val patients = _uniquePatients.value
                if (patients.isEmpty()) {
                    _message.value = "Aucun patient à exporter."
                    _isLoading.value = false
                    return@launch
                }
                ExcelUtil.writePatientsToExcel(context, uri, patients)
                _excelFileName.value = ExcelUtil.getFileName(context, uri)
                _message.value = "Export effectué : ${_excelFileName.value}"
            } catch (e: Exception) {
                _message.value = "Erreur lors de l'export : ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetMessage() {
        _message.value = null
    }

    fun getPatientNames(): List<String> {
        return _uniquePatients.value.map { it.nom }.distinct()
    }
}