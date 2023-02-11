package com.example.aankh.repository.remoteDataSource.tracking

import com.example.aankh.dataModels.*
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
    ): Call<UpdateResponse>


    @GET("patrolingofficers/{id}/profile")
    fun getUserProfile(@Path("id") userId: String): Call<UserProfileData>

    @GET("fetchemergency/{id}")
    fun getEmergencyCheckPoint(@Path("id") userId: String): Call<ArrayList<CheckPointsDataModel>>


    @POST("patrolingofficers/{id}/endtime")
    fun updateStopTime(
        @Path("id") userId: String,
        @Body stopTime: UserStopTime
    ): Call<UpdateResponse>

    @POST("patrolingofficers/{id}/starttime")
    fun updateStartTime(
        @Path("id") userId: String,
        @Body startTime: UserStartTime
    ): Call<UpdateResponse>


    @POST("patrolingofficers/{id}/report")
    fun postComplaintReport(
        @Path("id") userId: String,
        @Body report: ReportComplaintDataModel
    ): Call<UpdateResponse>
}