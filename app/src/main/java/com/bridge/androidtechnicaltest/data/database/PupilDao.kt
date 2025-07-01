package com.bridge.androidtechnicaltest.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PupilDao : PupilLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertPupil(pupil: PupilEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertPupils(pupils: List<PupilEntity>)

    @Query("SELECT * FROM pupils_table")
    override fun getAllPupils(): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE pupilId = :pupilId")
    override suspend fun getPupilById(pupilId: Int): PupilEntity?

    @Update
    override suspend fun updatePupil(pupil: PupilEntity)

    @Query("DELETE FROM pupils_table WHERE pupilId = :pupilId")
    override suspend fun deletePupilById(pupilId: Int)

    @Query("SELECT * FROM pupils_table WHERE is_synced = 0  OR is_synced IS NULL")
    override suspend fun getUnsyncedPupils(): List<PupilEntity>

    //  Search methods
    @Query("SELECT * FROM pupils_table WHERE name LIKE '%' || :query || '%'")
    override fun searchPupilsByName(query: String): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE country = :country")
    override fun filterPupilsByCountry(country: String): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE pupilId = :id")
    override suspend fun searchById(id: Long): PupilEntity?


    @Query("DELETE FROM pupils_table")
    override suspend fun deleteAllPupils()
}