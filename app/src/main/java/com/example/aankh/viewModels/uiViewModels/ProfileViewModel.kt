package com.example.aankh.viewModels.uiViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.dataModels.UserProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private var fetched: Boolean = false

    private var userProfileData = MutableLiveData<UserProfileData>()

    fun isFetched() = fetched

    fun getUserProfile() = userProfileData

    fun fetchUserProfile() {
        userProfileData = MutableLiveData()
        viewModelScope.launch {
//TODO make a get request for the profile data and then update the live data

            fetched = true
        }
    }

}