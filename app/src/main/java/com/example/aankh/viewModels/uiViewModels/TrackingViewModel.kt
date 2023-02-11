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


    fun getEmergencyCheckPoint(userId: String) {
        viewModelScope.launch { repository.getEmergencyCheckPoint(userId) }
    }


    fun getEmergencyCheckPointData() = repository.getEmergencyCheckPointData()
    fun getCheckPointsData() = repository.getCheckPointsData()


    fun postEndRoute(userId: String, locations: List<LatLng>) {
        viewModelScope.launch {

            repository.postEndRoute(userId, locations)
        }
    }


    fun postStartAndStopTime(userid: String, startTime: Long, stopTime: Long) {
        viewModelScope.launch {
            repository.postStartAndStopTime(userid, startTime, stopTime)
        }
    }
}