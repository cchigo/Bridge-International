package com.bridge.androidtechnicaltest.domain


import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.models.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.models.local.Pupil
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.models.remote.PupilDTOMapper
import com.bridge.androidtechnicaltest.ui.viewmodel.PendingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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

            if(localId == null){
                localDataSource.upsertPupil(pupilEntity)
            }else{
                localDataSource.insertPupil(pupilEntity)
            }

        } catch (e: Exception) {
        }
    }


    fun deletePupil(pupil: Pupil, localId: Int?): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        if (networkChecker.isConnected()) {

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
        } else {
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

    suspend fun mockInsert(pupil: PupilEntity){
        localDataSource.insertPupil(pupil)
    }

    fun createPupil(pupil: Pupil, localId: Int ?= null): Flow<BaseResponse<Pupil>> = flow {

        emit(BaseResponse.Loading)

        val entity = localMapper.to(pupil).run {
            if (localId != null) copy(id = localId, isSynced = false)
            else copy(isSynced = false)
        }


        if (!networkChecker.isConnected()) {
            insertInDb(entity, localId)
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
                emit(BaseResponse.Success(localMapper.from(syncedEntity)))
            }

            is BaseResponse.Error -> {
                insertInDb(entity, localId)
                emit(
                    BaseResponse.Error(
                        ErrorApiResponse("Network error. Pupil saved locally and will sync later.")
                    )
                )
            }

            else -> {
                insertInDb(entity, localId)
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
        }

    }



    fun syncPupils(): Flow<PendingViewModel.SyncResult> = flow {
        emit(PendingViewModel.SyncResult.Started)

        if (!networkChecker.isConnected()) {
            emit(
                PendingViewModel.SyncResult.Failure(
                    failedCount = -1 ,
                    "You are currently offline. please try again"
                )
            )
            return@flow
        }

        val failedPupils = mutableListOf<PupilEntity>()
        var syncedCount = 0

        val pupils = localDataSource.getUnsyncedPupils().first()

        pupils.forEach { entity ->
            delay(200L) // this is to Simulate  slight delay for realism

            val pupil = localMapper.from(entity)
            val remoteDto = dtoMapper.to(pupil)

            try {
                when (val result = repository.createPupil(remoteDto)) {
                    is BaseResponse.Success -> {
                        syncedCount++
                        emit(
                            PendingViewModel.SyncResult.Progress(
                                current = syncedCount,
                                total = pupils.size
                            )
                        )
                        localDataSource.deletePupilById(entity.id)
                    }

                    is BaseResponse.Error -> {
                        failedPupils.add(entity)
                    }

                    else -> Unit
                }
            } catch (e: Exception) {
                failedPupils.add(entity)
            }
        }

        if (failedPupils.isEmpty()) {
            emit(PendingViewModel.SyncResult.Success)
        } else {
            emit(PendingViewModel.SyncResult.Failure(failedCount = failedPupils.size))
        }
    }


    fun syncUnsyncedMockPupils(): Flow<PendingViewModel.SyncResult> = flow {
        emit(PendingViewModel.SyncResult.Started)

        val failedPupils = mutableListOf<PupilEntity>()
        var syncedCount = 0

        val list = localDataSource.getUnsyncedPupils().first()

        list.forEach { pupil ->
            delay(400L)

            val isSuccess = (0..99).random() < 30

            if (isSuccess) {
                syncedCount++
                emit(PendingViewModel.SyncResult.Progress(current = syncedCount, total = list.size))
                localDataSource.deletePupilById(pupil.id)
            } else {
                failedPupils.add(pupil)
            }
        }

        if (failedPupils.isEmpty()) {
            emit(PendingViewModel.SyncResult.Success)
        } else {
            emit(PendingViewModel.SyncResult.Failure(failedCount = failedPupils.size))
        }
    }




    suspend fun delete(localId: Int){
        try {

            localDataSource.deletePupilById(localId)
        }catch (e: Exception){

        }
    }

}