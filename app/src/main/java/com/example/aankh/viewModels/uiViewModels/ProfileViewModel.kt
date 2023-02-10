package com.example.aankh.viewModels.uiViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.repository.remoteDataSource.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {


    private val profileRepository = ProfileRepository()

    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            profileRepository.getUserProfile(userId)
        }
    }

    fun isFetched() = profileRepository.isFetched()
    fun getUserProfileData() = profileRepository.getUserProfileData()

}