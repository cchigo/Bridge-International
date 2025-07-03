package com.bridge.androidtechnicaltest.data.database

import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import kotlinx.coroutines.flow.Flow


interface PupilLocalDataSource {

    suspend fun insertPupil(pupil: PupilEntity)

    suspend fun insertPupils(pupils: List<PupilEntity>)

    fun getAllPupils(): Flow<List<PupilEntity>>

    suspend fun getPupilById(pupilId: Int): PupilEntity?

    suspend fun updatePupil(pupil: PupilEntity)

    suspend fun deletePupilById(pupilId: Int)

    suspend fun deleteAllPupils()

     fun getUnsyncedPupils(): Flow<List<PupilEntity>>

    fun searchPupilsByName(query: String): Flow<List<PupilEntity>>

    fun filterPupilsByCountry(country: String): Flow<List<PupilEntity>>

    suspend fun searchById(id: Long): PupilEntity?

    suspend fun delete(pupil: PupilEntity)

    suspend fun upsertPupil(pupil: PupilEntity)

}