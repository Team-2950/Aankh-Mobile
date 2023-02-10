package com.example.aankh.viewModels.uiViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.repository.remoteDataSource.TrackingRepository
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


    fun postCurrentLocation(userID: String, position: LatLng) {
        viewModelScope.launch {
            repository.updateCurrentLocation(userID, position)
        }
    }


    fun getCheckPointsData() = repository.getCheckPointsData()
}