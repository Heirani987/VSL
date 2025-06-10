package com.vsl.data.db

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PatientRepository(
    private val patientDao: PatientDao,
    private val context: Context
) {
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    suspend fun getAllPatientsList(): List<Patient> = patientDao.getAllPatientsList()

    suspend fun insert(patient: Patient) {
        patientDao.insert(patient)
    }

    suspend fun insertAll(patients: List<Patient>) {
        patientDao.insertAll(patients)
    }

    suspend fun update(patient: Patient) {
        patientDao.update(patient)
    }

    suspend fun delete(patient: Patient) {
        patientDao.delete(patient)
    }

    suspend fun deleteAll() {
        patientDao.deleteAll()
    }
}