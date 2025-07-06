package com.bridge.androidtechnicaltest.domain


import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.models.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.models.local.Pupil
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.models.remote.PupilDTOMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class PupilManagerUsecase @Inject constructor(
    private val repository: PupilsRepository,
    private val localDataSource: PupilLocalDataSource,
    private val dtoMapper: PupilDTOMapper,
    private val localMapper: EntityModelMapper,
    private val networkChecker: NetworkChecker
) {

    private suspend fun insertInDb(pupilEntity: PupilEntity, localId: Int?) {

        try {
            localDataSource.upsertPupil(pupilEntity)

        } catch (e: Exception) {
            Timber.d("error inserting pupil")
        }
    }


    fun deletePupil(pupil: Pupil, localId: Int?): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        insertInDb(localMapper.to(pupil).copy(
            id = localId!!,
            isSynced = true,
            isDeleted = true
        ), localId)


            when (val result = pupil.pupilId?.let { repository.deletePupil(it) }) {
                is BaseResponse.Success -> {

                    if (localId != null) {
                        localDataSource.deletePupilById(localId)
                    }
                    emit(BaseResponse.Success(pupil.copy(isDeleted = true, isSynced = true)))
                }
                is BaseResponse.Error -> emit(BaseResponse.Error(result.error))
                else -> Unit
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

            is BaseResponse.Error -> { emit(BaseResponse.Error(result.error)) }
            else -> {
                val unknownError = ErrorApiResponse(
                    title = "Unknown error occurred"
                )
                emit(BaseResponse.Error(unknownError))
            }
        }
    }


    fun createPupil(pupil: Pupil, localId: Int ?= null): Flow<BaseResponse<Pupil>> = flow {
      emit(BaseResponse.Loading)
        val entity_ = localMapper.to(pupil).copy(pupilId = null, isSynced = false,)

        val remoteDto = dtoMapper.to(localMapper.from(entity_))

        when (val result = repository.createPupil(remoteDto)) {
            is BaseResponse.Success -> {
                val remotePupil = dtoMapper.from(result.data)
                val syncedEntity = localMapper.to(remotePupil).copy(
                    isSynced = true
                )
                emit(BaseResponse.Success(localMapper.from(syncedEntity)))
            }

            is BaseResponse.Error -> {
                insertInDb(entity_, localId)
                emit(
                    BaseResponse.Error(
                        ErrorApiResponse("Network error. Pupil saved locally and will sync later.")
                    )
                )
            }
            else -> {
                insertInDb(entity_, localId)
                emit(BaseResponse.Error(ErrorApiResponse("Unknown error occurred.")))
            }
        }
    }

    fun getPupilByIdFromDB(pupilId: Int): Flow<Pupil> = flow {
        try {

            val localPupil = localDataSource.getPupilById(pupilId)
            if (localPupil != null) {
                val pupil = localMapper.from(localPupil)
                emit(pupil)
            }
        } catch (e: Exception) {
            Timber.d("error fetching pupil $e")
        }

    }

    suspend fun delete(localId: Int){
        try {

            localDataSource.deletePupilById(localId)
        }catch (e: Exception){
            Timber.d("error fetching pupil ${e.localizedMessage}")
        }
    }

}