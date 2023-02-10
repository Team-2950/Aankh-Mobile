package com.example.aankh.repository.remoteDataSource.tracking

import com.example.aankh.dataModels.CheckPointsDataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TrackingData {

    //    http://172.16.200.150:3000/patrolingofficers/12345/checkpoint
    @GET("patrolingofficers/{id}/checkpoint")
    fun getCheckPointsData(@Path("id") userId: String): Call<ArrayList<CheckPointsDataModel>>
}