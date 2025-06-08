// sert d’interface entre la base de données (Room) et la ViewModel.
//Il permet d’encapsuler la logique d’accès aux données
// (pour faciliter les évolutions futures, tests, etc).

package com.vsl.data.db

import kotlinx.coroutines.flow.Flow

class PatientRepository(private val patientDao: PatientDao) {

    fun getAllPatients() = patientDao.getAllPatients()

    suspend fun insert(patient: Patient) = patientDao.insert(patient)

    suspend fun insertAll(patients: List<Patient>) = patientDao.insertAll(patients)

    suspend fun update(patient: Patient) = patientDao.update(patient)

    suspend fun delete(patient: Patient) = patientDao.delete(patient)

    suspend fun deleteAll() = patientDao.deleteAll()

}