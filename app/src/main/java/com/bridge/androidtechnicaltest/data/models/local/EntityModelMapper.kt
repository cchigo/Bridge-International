package com.bridge.androidtechnicaltest.data.models.local

import com.bridge.androidtechnicaltest.common.BaseModelMapper

class EntityModelMapper: BaseModelMapper<Pupil, PupilEntity> {
    override fun from(data: PupilEntity): Pupil {
        return Pupil(
            name = data.name,
            country = data.country,
            image = data.image,
            latitude = data.latitude,
            longitude = data.longitude,
            isSynced = true,
            pupilId = data.pupilId
        )
    }

    override fun to(data: Pupil): PupilEntity {
        return PupilEntity(
            name = data.name ?: "" ,
            pupilId = data.pupilId ?: 0,
            country = data.country ?: "",
            image = data.image ?: "",
            latitude = data.latitude ?: 0.00,
            longitude = data.longitude ?: 0.00,
        )
    }

    fun fromList(data: List<PupilEntity>): List<Pupil> {
        return data.map { from(it) }
    }

    fun toList(data: List<Pupil>): List<PupilEntity> {
        return data.map { to(it) }
    }

}