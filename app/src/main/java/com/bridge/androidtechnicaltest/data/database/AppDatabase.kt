package com.bridge.androidtechnicaltest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity

@Database(entities = [PupilEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pupilDao(): PupilDao

    companion object{
        val DB_NAME = "pupilsDB"
    }
}