package com.bridge.androidtechnicaltest.domain

import android.util.Log
import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
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
            Log.e("", "DB insert failed: ${e.localizedMessage}")
        }
    }


    fun deletePupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)
        if (networkChecker.isConnected()) {

            when (val result = pupil.pupilId?.let { repository.deletePupil(it) }) {
                is BaseResponse.Success -> {

                    emit(BaseResponse.Success(pupil.copy(isDeleted = true, isSynced = true)))
                }
                is BaseResponse.Error -> emit(BaseResponse.Error(result.error))
                else -> Unit
            }
        }else{
            val error = ErrorApiResponse(
                title = "Unable to delete ${pupil.name}. Please retry when network is available."
            )
            emit(BaseResponse.Error(error))

        }

    }

    fun updatePupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        if (!networkChecker.isConnected() || pupil.pupilId == null) {
            val error = ErrorApiResponse(
                title = "Unable to update pupil. Please retry when network is available."
            )
            emit(BaseResponse.Error(error))
            return@flow
        }

        val pupilRemote = dtoMapper.to(pupil)
        when (val result = repository.updatePupil(pupil.pupilId, pupilRemote)) {
            is BaseResponse.Success -> {
                val updatedPupil = dtoMapper.from(result.data)
                emit(BaseResponse.Success(updatedPupil))
            }

            is BaseResponse.Error -> {
                emit(BaseResponse.Error(result.error))
            }


            else -> {
                val unknownError = ErrorApiResponse(
                    title = "Unknown error occurred"
                )
                emit(BaseResponse.Error(unknownError))
            }
        }
    }




    fun createPupil(pupil: Pupil): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        val entity = localMapper.to(pupil).copy(
            isSynced = false,
        )
        insertInDb(entity)

        if (!networkChecker.isConnected()) {
            val error = ErrorApiResponse(
                title = "We noticed you are offline. The pupil will be synced once you are back online."
            )
            emit(BaseResponse.Error(error))

            return@flow
        }

        val remoteDto = dtoMapper.to(localMapper.from(entity))

        when (val result = repository.createPupil(remoteDto)) {
            is BaseResponse.Success -> {
                val remotePupil = dtoMapper.from(result.data)
                val syncedEntity = localMapper.to(remotePupil).copy(
                    isSynced = true
                )
                insertInDb(syncedEntity) // Update local record as synced
                emit(BaseResponse.Success(localMapper.from(syncedEntity)))
            }

            is BaseResponse.Error -> {
                emit(
                    BaseResponse.Error(
                        ErrorApiResponse("Network error. Pupil saved locally and will sync later.")
                    )
                )
            }

            else -> {
                emit(BaseResponse.Error(ErrorApiResponse("Unknown error occurred.")))
            }
        }
    }

    fun getPupilByIdFromDB(pupilId: Int): Flow<Pupil> = flow {
        try {

            val localPupil = localDataSource.getPupilById(pupilId)


            if (localPupil != null) {
                val pupil = localMapper.from(localPupil)

                Log.d("PUPIL__TAG", "getPupilByIdFromDB: $localPupil")
                emit(pupil)
            }
        }catch (e: Exception){

            Log.d("PUPIL__TAG", "getPupilByIdFromDB erroe: $e")
        }

    }

//    suspend fun getPupilByName(name: String): Pupil? {
//        return localDataSource.getPupilByName(name)
//    }


}