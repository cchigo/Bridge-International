package com.bridge.androidtechnicaltest.domain

import android.util.Log
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.common.Utils.generateTimestamp
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PupilManagerUsecase @Inject constructor(
    private val repository: PupilsRepository,
    private val localDataSource: PupilLocalDataSource,
    private val dtoMapper: PupilDTOMapper,
    private val localMapper: EntityModelMapper,
    private val networkChecker: NetworkChecker
) {

    private suspend fun insertInDb(pupilEntity: PupilEntity) {
        try {
            localDataSource.insertPupil(pupilEntity)
        } catch (e: Exception) {
            Log.e("PUPILS_FLOW_UC", "DB insert failed: ${e.localizedMessage}")
        }
    }


    fun updatePupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        val localEntity = localMapper.to(pupil).copy(
            isSynced = false,
            timeStamp = generateTimestamp()
        )

        insertInDb(localEntity)

        if (!networkChecker.isConnected() || localEntity.pupilId == null) {
            emit(
                BaseResponse.Error(
                    ErrorApiResponse("Offline or unsynced pupil. Changes saved locally.")
                )
            )
            return@flow
        }

        // Safe to call update
        val pupilToUpdate = localMapper.from(localEntity)
        val pupilRemote = dtoMapper.to(pupilToUpdate)

        when (val result = repository.updatePupil(localEntity.pupilId, pupilRemote)) {
            is BaseResponse.Success -> {
                val updatedPupil = dtoMapper.from(result.data)

                val syncedEntity = localMapper.to(updatedPupil).copy(
                    isSynced = true,
                    timeStamp = generateTimestamp()
                )
                insertInDb(syncedEntity)
                emit(BaseResponse.Success(localMapper.from(syncedEntity)))
            }

            is BaseResponse.Error -> {
                emit(BaseResponse.Error(result.error))
            }

            else -> Unit
        }
    }



    fun createPupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        val entity = localMapper.to(pupil).copy(
            isSynced = false,
            timeStamp = generateTimestamp()
        )

        insertInDb(entity)

        if (networkChecker.isConnected()) {
            val remoteDto = dtoMapper.to(localMapper.from(entity))

            when (val result = repository.createPupil(remoteDto)) {
                is BaseResponse.Success -> {
                    val remotePupil = dtoMapper.from(result.data)
                    val syncedEntity = localMapper.to(remotePupil).copy(
                        isSynced = true,
                        timeStamp = generateTimestamp()
                    )
                    insertInDb(syncedEntity)
                    emit(BaseResponse.Success(localMapper.from(syncedEntity)))
                }

                is BaseResponse.Error -> {

                    emit(BaseResponse.Success(localMapper.from(entity)))
                }

                else -> {}
            }
        } else {
            Log.d("PUPILS_FLOW_UC", "Offline: Pupil saved locally and marked as unsynced.")
            emit(BaseResponse.Success(localMapper.from(entity)))
        }
    }

    fun deletePupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        val deletedEntity = localMapper.to(pupil).copy(
            isDeleted = true,
            isSynced = false,
            timeStamp = generateTimestamp()
        )
        insertInDb(deletedEntity)

        // Deleted locally because we dont have id to delete pupil if it was created without syncing
        if (pupil.pupilId == null || !networkChecker.isConnected()) {
            emit(BaseResponse.Success(localMapper.from(deletedEntity)))
            return@flow
        }


        when (val result = repository.deletePupil(pupil.pupilId)) {
            is BaseResponse.Success -> {
                localDataSource.deletePupilById(pupil.pupilId)
                emit(BaseResponse.Success(pupil.copy(isDeleted = true, isSynced = true)))
            }
            is BaseResponse.Error -> emit(BaseResponse.Error(result.error))
            else -> Unit
        }


    }


}