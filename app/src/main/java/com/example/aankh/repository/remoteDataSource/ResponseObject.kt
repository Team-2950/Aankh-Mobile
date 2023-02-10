package com.example.aankh.repository.remoteDataSource

import com.example.aankh.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ResponseObject {

    val trackingData: TrackingData

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        trackingData = retrofit.create(TrackingData::class.java)
    }
}