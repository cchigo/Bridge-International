package com.bridge.androidtechnicaltest.data.models.remote

import com.bridge.androidtechnicaltest.common.BaseModelMapper
import com.bridge.androidtechnicaltest.common.Utils.generateTimestamp
import com.bridge.androidtechnicaltest.data.models.local.Pupil
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO

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
            pupilId = data.pupilId ?: 0,
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

    fun convertDtoListToEntityList(dtoList: List<PupilDTO>) = dtoList.map {
        with(it) {
            PupilEntity(
                pupilId = pupilId,
                name = name ?: "",
                country = country ?: "",
                image = image ?: "",
                latitude = latitude ?: 0.0,
                longitude = longitude ?: 0.0,
                isSynced = true
            )
        }
    }

}