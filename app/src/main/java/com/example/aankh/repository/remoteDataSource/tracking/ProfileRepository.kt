package com.example.aankh.repository.remoteDataSource.tracking

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.UserProfileData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {
    private var userProfileData = MutableLiveData<UserProfileData>()
    private var fetched: Boolean = false

    fun getUserProfile(userId: String) {
        val data = ResponseObject.trackingData.getUserProfile(userId
        )
        try {
            data.enqueue(object : Callback<UserProfileData> {
                override fun onResponse(
                    call: Call<UserProfileData>,
                    response: Response<UserProfileData>
                ) {
                    val data = response.body()!!
                    if (data != null) {
                        userProfileData.postValue(data)
                        fetched = true
                        Log.d("profile repo onResp", "fetched")
                    }
                }

                override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                    Log.d("profile repo onFail", "error")
                }

            })
        } catch (e: Exception) {
            Log.d("profile repo", "error")
        }
    }


    fun getUserProfileData() = userProfileData

    fun isFetched() = fetched
}