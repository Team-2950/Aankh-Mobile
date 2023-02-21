package com.example.aankh.viewModels.uiViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.dataModels.ReportComplaintDataModel
import com.example.aankh.dataModels.UpdateResponse
import com.example.aankh.repository.remoteDataSource.tracking.ResponseObject
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportComplaintViewModel : ViewModel() {

    private val status = MutableLiveData<String>()

    fun reportComplaint(userId: String, report: ReportComplaintDataModel) {

        try {
            viewModelScope.launch {
                ResponseObject.trackingData.postComplaintReport(userId, report).enqueue(object :
                    Callback<UpdateResponse> {
                    override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                    ) {
                        val data = response.body()
                        if (data != null && data.status == "success") {
                            status.postValue(data?.toString())
                            Log.d("time update", "stop time send successfully ")
                        } else {
                            Log.d("time update", "stop time not sent successfully ")

                        }
                    }

                    override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {

                    }

                })
            }
        } catch (e: Exception) {
            Log.d("complaint report", "error onCatch")
        }
    }


    fun getReportStatus() = status
}