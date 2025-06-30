package com.bridge.androidtechnicaltest.db;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PupilDao {

    @Query("SELECT * FROM Pupils ORDER BY name ASC")
    Single<List<Pupil>> getPupils();
}
