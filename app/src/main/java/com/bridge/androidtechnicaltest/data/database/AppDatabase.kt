package com.bridge.androidtechnicaltest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.bridge.androidtechnicaltest.data.model.pupil.local.RemoteKeys

@Database(
    entities = [PupilEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pupilDao(): PupilDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        const val DB_NAME = "pupilsDB"
    }
}
