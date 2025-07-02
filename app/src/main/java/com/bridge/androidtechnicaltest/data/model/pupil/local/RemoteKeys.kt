package com.bridge.androidtechnicaltest.data.model.pupil.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val pupilId: Int,
    val currentPage: Int
)


