package com.bridge.androidtechnicaltest.domain

import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.ErrorApiResponse
import com.bridge.androidtechnicaltest.common.NetworkChecker
import com.bridge.androidtechnicaltest.data.database.PupilLocalDataSource
import com.bridge.androidtechnicaltest.data.models.local.EntityModelMapper
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.models.remote.PupilDTOMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
    private val localDataSource: PupilLocalDataSource
) {

    fun getPupilById(pupilId: Int): Flow<BaseResponse<PupilEntity>> = flow {
        emit(BaseResponse.Loading)

        val localPupil = localDataSource.getPupilById(pupilId)
        if (localPupil != null) {

            emit(BaseResponse.Success(localPupil))
            return@flow
        }
            when (val result = repository.getPupilById(pupilId)) {
                is BaseResponse.Success -> {

                    emit(BaseResponse.Success(  PupilEntity(
                        name = result.data.name ?: "" ,
                        pupilId = result.data.pupilId,
                        country = result.data.country ?: "",
                        image = result.data.image ?: "",
                        latitude = result.data.latitude ?: 0.00,
                        longitude = result.data.longitude ?: 0.00,
                    )
                    ))
                }

                is BaseResponse.Error -> {
                    emit(BaseResponse.Error(ErrorApiResponse(title = result.error.title)))
                }

                else -> {
                    emit(BaseResponse.Error(ErrorApiResponse(title = "Pupil not found. Please try again.")))
                }
            }
    }

    fun getUnsyncedPupilsFromDB(): Flow<List<PupilEntity>> {
        return localDataSource.getAllPendingItems()
            .catch { e ->
                emit(emptyList())
            }
    }



}

