package com.bridge.androidtechnicaltest.data.model.pupil.local

// app wide model
data class Pupil(
    val country: String? = null,
    val image: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val name: String? = null,
    val pupilId: Int? = null,
    val isSynced: Boolean ?= null,
    val timeStamp : String ?= null,
    val isDeleted: Boolean ?= null,
)