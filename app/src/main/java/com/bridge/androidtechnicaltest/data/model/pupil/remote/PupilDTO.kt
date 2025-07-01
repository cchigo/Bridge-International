package com.bridge.androidtechnicaltest.data.model.pupil.remote

//api model
data class PupilDTO(
    val country: String? = null,
    val image: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val name: String? = null,
    val pupilId: Int? = null
)


data class PupilsDTOResponse(
    val itemCount: Int? = null,
    val items: List<PupilDTO>? = null,
    val pageNumber: Int? = null,
    val totalPages: Int? = null
)

