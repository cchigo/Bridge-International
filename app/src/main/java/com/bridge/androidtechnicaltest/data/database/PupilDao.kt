package com.bridge.androidtechnicaltest.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.bridge.androidtechnicaltest.data.models.local.PupilEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PupilDao : PupilLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insertPupil(pupil: PupilEntity)

    @Upsert
    override suspend fun upsertPupil(pupil: PupilEntity)


    @Upsert
    suspend fun upsertAll(pupil: List<PupilEntity>)


    @Query("SELECT * FROM pupils_table ORDER BY id DESC")
    override fun getAllPupils(): Flow<List<PupilEntity>>


    @Query("SELECT * FROM pupils_table WHERE id = :pupilId")
    override suspend fun getPupilById(pupilId: Int): PupilEntity?

    @Update
    override suspend fun updatePupil(pupil: PupilEntity)


    @Query("SELECT * FROM pupils_table WHERE is_synced = 1 AND is_deleted = 0 ")
    fun getPagedSyncedPupils(): PagingSource<Int, PupilEntity>

    @Query("DELETE FROM pupils_table WHERE id = :localId")
    override suspend fun deletePupilById(localId: Int)


    @Query("DELETE FROM pupils_table WHERE is_synced = 1 AND is_deleted = 0")
    override suspend fun deleteAllPupils()


    @Query("SELECT * FROM pupils_table WHERE is_synced = 0 ORDER BY time_stamp DESC")
    override  fun getUnsyncedPupils(): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE  is_deleted = 1")
    override fun getDeleteList(): Flow<List<PupilEntity>>

    @Query("SELECT * FROM pupils_table WHERE is_synced = 0 OR is_deleted = 1 ORDER BY time_stamp DESC")
    override fun getAllPendingItems(): Flow<List<PupilEntity>>

    @Delete
    override suspend fun delete(pupil: PupilEntity)


}