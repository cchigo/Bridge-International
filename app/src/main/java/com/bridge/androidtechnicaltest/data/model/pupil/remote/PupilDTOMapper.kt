package com.bridge.androidtechnicaltest.data.model.pupil.remote

import com.bridge.androidtechnicaltest.common.BaseModelMapper
import com.bridge.androidtechnicaltest.common.Utils.generateTimestamp
import com.bridge.androidtechnicaltest.data.model.pupil.local.Pupil

class PupilDTOMapper : BaseModelMapper<Pupil, PupilDTO> {
    override fun from(data: PupilDTO): Pupil {
        return Pupil(
            name = data.name,
            pupilId = data.pupilId ?: -1 ,
            country = data.country,
            image = data.image,
            latitude = data.latitude,
            longitude = data.longitude,
            isSynced = false,
            timeStamp = generateTimestamp()
        )
    }

    override fun to(data: Pupil): PupilDTO {
        return PupilDTO(
            pupilId = data.pupilId,
            name = data.name,
            image = data.image,
            country = data.country,
            longitude = data.longitude,
            latitude = data.latitude
        )
    }
    fun toList(data: List<Pupil>): List<PupilDTO> {
        return data.map { to(it) }
    }

    fun fromList(data: List<PupilDTO>?): List<Pupil>{
        if (data != null) {
            return data.map { dto ->
                from(dto)  }
        }
       return emptyList()
    }
}