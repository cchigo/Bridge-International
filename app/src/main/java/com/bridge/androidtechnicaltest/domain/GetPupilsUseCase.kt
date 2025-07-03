package com.bridge.androidtechnicaltest.domain

import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.common.Utils.generateTimestamp
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.model.pupil.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTOMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * This fetches pupils from local DB and updates it with remote data if there's any change.
 *
 * Always emits local data first, then checks for updates from the API and also, check of remote data is different from local first before saving.
 * This ensures offline support while keeping data fresh when online.
 */
class GetPupilsUseCase @Inject constructor(
    private val repository: PupilsRepository,
    private val localDataSource: PupilLocalDataSource,
    private val dtoMapper: PupilDTOMapper,
    private val localMapper: EntityModelMapper,
    private val networkChecker: NetworkChecker
) {


    fun getPupils(page: Int = 1): Flow<BaseResponse<List<PupilEntity>>> = flow {
        emit(BaseResponse.Loading)

        val localData = getLocalPupils().first()

        if (networkChecker.isConnected()) {
            when (val result = repository.getPupils(page)) {

                is BaseResponse.Success -> {
                    val pupilRemoteList = result.data.items
                    saveRemoteListToDb(pupilRemoteList)
                }

                is BaseResponse.Error -> {
                    emit(BaseResponse.Error(result.error))
                }

                else -> {
                    BaseResponse.Error(ErrorApiResponse("Unable to fetch pupils, please try again"))
                }
            }
        } else if (localData.isEmpty()) {
            emit(BaseResponse.Error(ErrorApiResponse("No internet connection")))
        }

        emitAll(getLocalPupils().map { BaseResponse.Success(it) })

    }


    private suspend fun saveRemoteListToDb(pupilRemoteList: List<PupilDTO>?) {
        val mappedRemote = localMapper.toList(dtoMapper.fromList(pupilRemoteList))
        localDataSource.insertPupils(mappedRemote)
    }


    private fun getLocalPupils(): Flow<List<PupilEntity>> {
        return localDataSource.getAllPupils()
    }


    fun getPupilById(pupilId: Int): Flow<BaseResponse<Pupil>> = flow {
        emit(BaseResponse.Loading)

        val localPupil = localDataSource.getPupilById(pupilId)
        var emittedLocal = false

        if (localPupil != null) {
            val pupil = localMapper.from(localPupil)
            emit(BaseResponse.Success(pupil))
            emittedLocal = true
        }

        if (networkChecker.isConnected()) {
            when (val result = repository.getPupilById(pupilId)) {
                is BaseResponse.Success -> {
                    val remotePupil = result.data
                    val local = PupilEntity(
                        pupilId = remotePupil.pupilId,
                        name = remotePupil.name,
                        country = remotePupil.country,
                        image = remotePupil.image,
                        latitude = remotePupil.latitude,
                        longitude = remotePupil.longitude,
                        isSynced = true,
                    )
                    localDataSource.insertPupil(local)
                }

                is BaseResponse.Error -> {
                    if (!emittedLocal) {
                        emit(BaseResponse.Error(result.error))
                    }
                }

                else -> {
                    if (!emittedLocal) {
                        emit(BaseResponse.Error(ErrorApiResponse("Pupil not found, please try again")))
                    }
                }
            }
        } else if (localPupil == null) {
            emit(BaseResponse.Error(ErrorApiResponse("No internet and no local data")))
        }
    }

}