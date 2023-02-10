package com.example.aankh.repository.remoteDataSource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.CheckPointsDataModel
import com.example.aankh.dataModels.CurrentLocationDataModel
import com.example.aankh.dataModels.CurrentLocationUpdateResponse
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingRepository {

    private var checkPointsData = MutableLiveData<ArrayList<CheckPointsDataModel>>()
    fun getCheckPoints(userId: String) {

        Log.d("check points user id", userId)
        val data = ResponseObject.trackingData.getCheckPointsData("12345")
        try {
            data.enqueue(object : Callback<ArrayList<CheckPointsDataModel>> {
                override fun onResponse(
                    call: Call<ArrayList<CheckPointsDataModel>>,
                    response: Response<ArrayList<CheckPointsDataModel>>
                ) {
                    val data: ArrayList<CheckPointsDataModel> = response.body()!!
                    if (data != null) {
                        checkPointsData.postValue(data)
                        Log.d("check points", data.size.toString())
                    }
                }

                override fun onFailure(call: Call<ArrayList<CheckPointsDataModel>>, t: Throwable) {
                    Log.d("check points on faliure", "error")
                }
            })
        } catch (e: Exception) {
            Log.d("check points", "error")
        }

    }

    fun getCheckPointsData(): MutableLiveData<ArrayList<CheckPointsDataModel>> = checkPointsData


    fun updateCurrentLocation(userId: String, position: LatLng) {
        try {

            val currentPosition = CurrentLocationDataModel(
                position.longitude,
                position.latitude
            )
            ResponseObject.trackingData.updateCurrentPosition(userId, currentPosition)
                .enqueue(object : Callback<CurrentLocationUpdateResponse> {
                    override fun onResponse(
                        call: Call<CurrentLocationUpdateResponse>,
                        response: Response<CurrentLocationUpdateResponse>
                    ) {
                        val data = response.body()
                        if (data != null) {
                            Log.d("current location update", "send successfully ")
                        }

                    }

                    override fun onFailure(
                        call: Call<CurrentLocationUpdateResponse>,
                        t: Throwable
                    ) {

                    }

                })
        } catch (e: Exception) {
        }
    }
}