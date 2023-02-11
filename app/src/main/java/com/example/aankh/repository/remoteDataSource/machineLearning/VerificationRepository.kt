package com.example.aankh.repository.remoteDataSource.machineLearning

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.ImageMatchingResultDataModel
import com.example.aankh.dataModels.UpdateResponse
import com.example.aankh.dataModels.VerificationImageDataModel
import com.example.aankh.repository.remoteDataSource.machineLearning.ModelResponseObject.mlModelData
import com.example.aankh.repository.remoteDataSource.tracking.ResponseObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationRepository {
    private var result = MutableLiveData<String>()

    fun compareImage(userid: String, imageSet: VerificationImageDataModel) {
        try {
            mlModelData.getImageMatchingResult(imageSet)
                .enqueue(object : Callback<ImageMatchingResultDataModel> {
                    @SuppressLint("NullSafeMutableLiveData")
                    override fun onResponse(
                        call: Call<ImageMatchingResultDataModel>,
                        response: Response<ImageMatchingResultDataModel>
                    ) {
                        val data = response.body()
                        if (data != null) {

                            ResponseObject.trackingData.updateVerificationData(userid, data)
                                .enqueue(object : Callback<UpdateResponse> {
                                    override fun onResponse(
                                        call: Call<UpdateResponse>,
                                        response: Response<UpdateResponse>
                                    ) {
                                        result.postValue("success")
                                    }

                                    override fun onFailure(
                                        call: Call<UpdateResponse>,
                                        t: Throwable
                                    ) {

                                    }

                                })

                        }
                    }

                    override fun onFailure(call: Call<ImageMatchingResultDataModel>, t: Throwable) {

                    }

                })

        } catch (e: Exception) {

        }
    }


    fun getResult() = result

}