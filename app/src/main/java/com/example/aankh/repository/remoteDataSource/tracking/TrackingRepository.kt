package com.example.aankh.repository.remoteDataSource.tracking

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.CheckPointsDataModel
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingRepository {

    private var checkPointsData = MutableLiveData<ArrayList<CheckPointsDataModel>>()
    fun getCheckPoints(userId: String) {

        Log.d("check points user id", userId)
        val data = ResponseObject.trackingData.getCheckPointsData("12345")
        data.enqueue(object : Callback<ArrayList<CheckPointsDataModel>> {
            override fun onResponse(
                call: Call<ArrayList<CheckPointsDataModel>>,
                response: Response<ArrayList<CheckPointsDataModel>>
            ) {
//                Log.d("check points repo", response.body().toString())
                val data: ArrayList<CheckPointsDataModel> = response.body()!!
                if (data != null) {
                    checkPointsData.postValue(data)
                }
            }

            override fun onFailure(call: Call<ArrayList<CheckPointsDataModel>>, t: Throwable) {
            }
        })

    }

    fun getCheckPointsData(): MutableLiveData<ArrayList<CheckPointsDataModel>> = checkPointsData


    fun updateCurrentLocation(position: LatLng) {

    }
}