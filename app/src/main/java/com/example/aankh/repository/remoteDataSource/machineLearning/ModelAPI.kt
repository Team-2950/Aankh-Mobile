package com.example.aankh.repository.remoteDataSource.machineLearning

import com.example.aankh.dataModels.ImageMatchingResultDataModel
import com.example.aankh.dataModels.VerificationImageDataModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ModelAPI {

    @POST("predict")
    fun getImageMatchingResult(@Body imageSet: VerificationImageDataModel): Call<ImageMatchingResultDataModel>
}