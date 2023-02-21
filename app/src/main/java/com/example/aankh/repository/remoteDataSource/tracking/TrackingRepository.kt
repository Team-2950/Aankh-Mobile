package com.example.aankh.repository.remoteDataSource.tracking

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.*
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackingRepository {

    private var checkPointsData = MutableLiveData<ArrayList<CheckPointsDataModel>>()
    private var emergencyCheckPoints = MutableLiveData<ArrayList<CheckPointsDataModel>>()


    fun getCheckPoints(userId: String) {

        Log.d("check points user id", userId)
        val data = ResponseObject.trackingData.getCheckPointsData(userId)
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
                .enqueue(object : Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        val data = response.body()
                        if (data != null && data.status == "success") {
                            Log.d("current location update", "send successfully ")
                        }

                    }

                    override fun onFailure(
                        call: Call<UpdateResponse>,
                        t: Throwable
                    ) {

                    }

                })
        } catch (e: Exception) {
        }
    }


    fun getEmergencyCheckPoint(userId: String) {
        try {
//            TODO change this to user ID
            ResponseObject.trackingData.getEmergencyCheckPoint(userId)
                .enqueue(object : Callback<ArrayList<CheckPointsDataModel>> {
                    override fun onResponse(
                        call: Call<ArrayList<CheckPointsDataModel>>,
                        response: Response<ArrayList<CheckPointsDataModel>>
                    ) {
                        val dataSet = response.body()!!
                        if (dataSet != null && dataSet.isNotEmpty()) {
                            Log.d("tracking repo", "success")

                            if (emergencyCheckPoints.value?.size != dataSet.size) {


                                emergencyCheckPoints.value?.clear()

                                emergencyCheckPoints.postValue(dataSet)

                                Log.d("tracking repo", "updated the checkpoints")

                            }

                            Log.d("tracking repo", "fetching again and again...")

                        }

                    }

                    override fun onFailure(
                        call: Call<ArrayList<CheckPointsDataModel>>,
                        t: Throwable
                    ) {
                        Log.d("tracking repo", "error onFailure")
                    }


                })

        } catch (e: Exception) {
            Log.d("tracking repo", "error catched")
        }

    }


    fun getEmergencyCheckPointData() = emergencyCheckPoints

    fun postStartAndStopTime(userId: String, startTime: Long, stopTime: Long) {
        Log.d("time update", "userid ${userId}")

        val start = UserStartTime(startTime)
        val stop = UserStopTime(stopTime)

        try {
            ResponseObject.trackingData.updateStartTime(userId, start)
                .enqueue(object : Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        val data = response.body()
                        if (data != null && data.status == "success") {
                            Log.d("time update", "start time send successfully")
                        } else {
                            Log.d("time update", "start time not sent successfully ")

                        }
                    }

                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                        Log.d("time update", "error onFaliure")
                    }

                })
            ResponseObject.trackingData.updateStopTime(userId, stop)
                .enqueue(object : Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        val data = response.body()
                        if (data != null && data.status == "success") {
                            Log.d("time update", "stop time send successfully ")
                        } else {
                            Log.d("time update", "stop time not sent successfully ")

                        }
                    }

                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                        Log.d("time update", "error onFaliure")

                    }
                })
        } catch (e: Exception) {
            Log.d("time update", "error onCatch")
        }
    }


    fun postEndRoute(userId: String, locations: List<LatLng>) {

    }
}