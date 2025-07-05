package com.bridge.androidtechnicaltest.data.network

import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilDTO
import com.bridge.androidtechnicaltest.data.model.pupil.remote.PupilsDTOResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface PupilApi {

    @GET("pupils")
    suspend fun getPupils(@Query("page") page: Int? = 1): PupilsDTOResponse


    @GET("pupils/{pupilId}")
    suspend fun getPupilById(@Path("pupilId") pupilId: Int): PupilDTO


    @POST("pupils")
    suspend fun createPupil(@Body pupilId: PupilDTO): PupilDTO

    @PUT("pupils/{id}")
    suspend fun updatePupil(
        @Path("id") pupilId: Int,
        @Body updatedPupil: PupilDTO
    ): PupilDTO


    @DELETE("pupils/{id}")
    suspend fun deletePupilById(
        @Path("id") pupilId: Int
    )


}