package com.bridge.androidtechnicaltest.domain

import com.bridge.androidtechnicaltest.common.BaseResponse
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilsDTOResponse

interface PupilsRepository {
    suspend fun getPupils(page: Int): BaseResponse<PupilsDTOResponse>
    suspend fun getPupilById(id: Int): BaseResponse<PupilDTO>
    suspend fun createPupil(pupil: PupilDTO): BaseResponse<PupilDTO>
    suspend fun updatePupil(id: Int, pupil: PupilDTO): BaseResponse<PupilDTO>
    suspend fun deletePupil(id: Int): BaseResponse<Unit>
}
