package com.bridge.androidtechnicaltest.domain

import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.common.GeneralErrorHandlerImpl
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilsDTOResponse
import com.bridge.androidtechnicaltest.data.network.PupilApi
import javax.inject.Inject

class PupilRepositoryImpl @Inject constructor(
    private val api: PupilApi
) : PupilsRepository {

    override suspend fun getPupils(page: Int): BaseResponse<PupilsDTOResponse> {
        return try {
            val response = api.getPupils(page)
            BaseResponse.Success(response)
        } catch (e: Throwable) {
            val error = GeneralErrorHandlerImpl.getError(e)
            error
        }
    }


    override suspend fun getPupilById(id: Int): BaseResponse<PupilDTO> {
        return try {
            val response = api.getPupilById(id)
            BaseResponse.Success(response)
        } catch (e: Throwable) {
            GeneralErrorHandlerImpl.getError(e)
        }
    }

    override suspend fun createPupil(pupil: PupilDTO): BaseResponse<PupilDTO> {
        return try {
            val response = api.createPupil(pupil)
            BaseResponse.Success(response)
        } catch (e: Throwable) {
            GeneralErrorHandlerImpl.getError(e)
        }
    }

    override suspend fun updatePupil(id: Int, pupil: PupilDTO): BaseResponse<PupilDTO> {
        return try {
            val response = api.updatePupil(id, pupil)
            BaseResponse.Success(response)
        } catch (e: Throwable) {
            GeneralErrorHandlerImpl.getError(e)
        }
    }

    override suspend fun deletePupil(id: Int): BaseResponse<Unit> {
        return try {
            api.deletePupilById(id)
            BaseResponse.Success(Unit)
        } catch (e: Throwable) {
            GeneralErrorHandlerImpl.getError(e)
        }
    }




}
