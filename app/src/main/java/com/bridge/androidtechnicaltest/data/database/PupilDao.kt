package com.bridge.androidtechnicaltest.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bridge.androidtechnicaltest.data.model.pupil.local.PupilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PupilDao : PupilLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertPupil(pupil: PupilEntity)

    @Upsert
    override suspend fun upsertPupil(pupil: PupilEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertPupils(pupils: List<PupilEntity>)

//    @Query("SELECT * FROM pupils_table")
//    override fun getAllPupils(): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table ORDER BY id DESC")
    override fun getAllPupils(): Flow<List<PupilEntity>>


    @Query("SELECT * FROM pupils_table WHERE id = :pupilId")
    override suspend fun getPupilById(pupilId: Int): PupilEntity?

    @Update
    override suspend fun updatePupil(pupil: PupilEntity)



    @Query("SELECT * FROM pupils_table WHERE is_synced = 1 ORDER BY pupilId DESC")
    fun getPagedPupils(): PagingSource<Int, PupilEntity>



    @Query("SELECT * FROM pupils_table WHERE is_synced = 1 ORDER BY time_stamp DESC")
    fun getPagedSyncedPupils(): PagingSource<Int, PupilEntity>



    //  Search methods
    @Query("SELECT * FROM pupils_table WHERE name LIKE '%' || :query || '%'")
    override fun searchPupilsByName(query: String): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE country = :country")
    override fun filterPupilsByCountry(country: String): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE pupilId = :id")
    override suspend fun searchById(id: Long): PupilEntity?

    @Query("DELETE FROM pupils_table WHERE id = :localId")
    override suspend fun deletePupilById(localId: Int)

    @Query("SELECT * FROM pupils_table WHERE is_deleted = 1")
    suspend fun getDeletedPupils(): List<PupilEntity>

    @Query("DELETE FROM pupils_table")
    override suspend fun deleteAllPupils()

    @Query("SELECT * FROM pupils_table WHERE is_synced = 0 ORDER BY time_stamp DESC")
    override  fun getUnsyncedPupils(): Flow<List<PupilEntity>>


    @Delete
    override suspend fun delete(pupil: PupilEntity)



}