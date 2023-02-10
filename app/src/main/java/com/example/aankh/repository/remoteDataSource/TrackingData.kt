package com.example.aankh.repository.remoteDataSource

import com.example.aankh.dataModels.CheckPointsDataModel
import com.example.aankh.dataModels.CurrentLocationDataModel
import com.example.aankh.dataModels.CurrentLocationUpdateResponse
import com.example.aankh.dataModels.UserProfileData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TrackingData {

    //    http://192.168.43.148:3000/patrolingofficers/12345/checkpoint
    @GET("patrolingofficers/{id}/checkpoint")
    fun getCheckPointsData(@Path("id") userId: String): Call<ArrayList<CheckPointsDataModel>>

    @POST("patrolingofficers/{id}/updatecurrentlocation")
    fun updateCurrentPosition(
        @Path("id") userId: String,
        @Body currentPosition: CurrentLocationDataModel
    ): Call<CurrentLocationUpdateResponse>


    @GET("patrolingofficers/{id}/profile")
    fun getUserProfile(@Path("id") userId: String): Call<UserProfileData>
}