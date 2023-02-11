package com.example.aankh.repository.remoteDataSource.machineLearning

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.ImageMatchingResultDataModel
import com.example.aankh.dataModels.VerificationImageDataModel
import com.example.aankh.repository.remoteDataSource.machineLearning.ModelResponseObject.mlModelData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationRepository {
    private var result = MutableLiveData<ImageMatchingResultDataModel>()

    fun compareImage(imageSet: VerificationImageDataModel) {
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
                            result.postValue(data)
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