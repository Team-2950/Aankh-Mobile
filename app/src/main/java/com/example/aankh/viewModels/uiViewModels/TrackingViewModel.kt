package com.example.aankh.viewModels.uiViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.repository.remoteDataSource.tracking.TrackingRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class TrackingViewModel : ViewModel() {


    private val repository = TrackingRepository()


    fun fetchCheckPoints(userID: String) {
        viewModelScope.async {
            repository.getCheckPoints(userID)
        }
    }


    fun postCurrentLocation(position: LatLng) {
        viewModelScope.launch {
            repository.updateCurrentLocation(position)
        }
    }


    fun getCheckPointsData() = repository.getCheckPointsData()
}