package com.bridge.androidtechnicaltest.data.model.pupil.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey



@Entity(
    tableName = "pupils_table",
    indices = [Index(value = ["pupilId"], unique = true)]
)
data class PupilEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = 0 ,

    @ColumnInfo(name = "pupilId")
    val pupilId: Int?,

    @ColumnInfo(name = "name")
    val name: String?, //todo: split this into first and last name

    @ColumnInfo(name = "country")
    val country: String ?,

    @ColumnInfo(name = "image")
    val image: String?,

    @ColumnInfo(name = "latitude")
    val latitude: Double ?,

    @ColumnInfo(name = "longitude")
    val longitude: Double?,

    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean? = false,

    @ColumnInfo(name = "time_stamp")
    val timeStamp: String? = null,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean? = false,
)

//sort by time, name, clocation