package com.bridge.androidtechnicaltest.data.database

import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import kotlinx.coroutines.flow.Flow


interface PupilLocalDataSource {

    suspend fun insertPupil(pupil: PupilEntity)
    fun getAllPupils(): Flow<List<PupilEntity>>
    suspend fun getPupilById(pupilId: Int): PupilEntity?
    suspend fun updatePupil(pupil: PupilEntity)
    suspend fun deletePupilById(pupilId: Int)
    suspend fun deleteAllPupils()
    fun getUnsyncedPupils(): Flow<List<PupilEntity>>
    fun getAllPendingItems(): Flow<List<PupilEntity>>
    fun getDeleteList(): Flow<List<PupilEntity>>
    suspend fun delete(pupil: PupilEntity)
    suspend fun upsertPupil(pupil: PupilEntity)

}