package com.example.aankh.repository.remoteDataSource.machineLearning

import com.example.aankh.repository.remoteDataSource.tracking.TrackingData
import com.example.aankh.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ModelResponseObject {

    val mlModelData: ModelAPI

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.MODLE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mlModelData = retrofit.create(ModelAPI::class.java)
    }
}