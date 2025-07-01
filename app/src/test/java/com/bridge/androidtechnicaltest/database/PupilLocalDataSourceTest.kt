package com.bridge.androidtechnicaltest.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bridge.androidtechnicaltest.data.database.AppDatabase
import com.bridge.androidtechnicaltest.data.database.PupilDao
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PupilLocalDataSourceTest {

    private lateinit var userDao: PupilDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.pupilDao()
    }

    @Test
    @Throws(Exception::class)
    fun shouldInsertAndRetrieveSinglePupil() = runTest {

        val initialList = userDao.getAllPupils().first()
        assertThat(initialList).isEmpty()

        val pupil = PupilEntity(
            pupilId = 2,
            name = "New globe",
            country = "Nigeria",
            image = "www.thisisanimageurl.com",
            latitude = 2.234,
            longitude = 1.234,
            isSynced = true,
            timeStamp = "1234567"
        )

        userDao.insertPupil(pupil)

        val updatedList = userDao.getAllPupils().first()
        assertThat(updatedList).hasSize(1)

        assertThat(updatedList).contains(pupil.copy(id = updatedList[0].id))
    }

    @Test
    fun shouldUpdateExistingPupil() = runTest {
        val pupil = PupilEntity(
            pupilId = 3,
            name = "Ada",
            country = "Ghana",
            image = "",
            latitude = 0.0,
            longitude = 0.0,
            isSynced = false,
            timeStamp = "2"
        )

        userDao.insertPupil(pupil)

        val saved = userDao.getPupilById(3)!!
        val updated = saved.copy(name = "Ada Updated")

        userDao.updatePupil(updated)

        val result = userDao.getPupilById(3)
        assertThat(result?.name).isEqualTo("Ada Updated")
    }

    @Test
    fun shouldDeletePupilById() = runTest {
        val pupil = PupilEntity(
            pupilId = 4,
            name = "Delete Student",
            country = "Kenya",
            image = "",
            latitude = 0.0,
            longitude = 0.0,
            isSynced = false,
            timeStamp = "3"
        )

        userDao.insertPupil(pupil)
        userDao.deletePupilById(4)

        val result = userDao.getPupilById(4)
        assertThat(result).isNull()
    }

    @Test
    fun shouldReturnOnlyUnsyncedPupils() = runTest {
        val pupils = listOf(
            PupilEntity(pupilId = 7, name = "Ade", country = "", image = "", latitude = 0.0, longitude = 0.0, isSynced = true, timeStamp = ""),
            PupilEntity(pupilId = 8, name = "Bayo", country = "", image = "", latitude = 0.0, longitude = 0.0, isSynced = false, timeStamp = ""),
            PupilEntity(pupilId = 9, name = "Chioma", country = "", image = "", latitude = 0.0, longitude = 0.0, isSynced = null, timeStamp = "")
        )

        userDao.insertPupils(pupils)

        val result = userDao.getUnsyncedPupils()
        assertThat(result.map { it.pupilId }).containsExactly(8, 9)
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}
