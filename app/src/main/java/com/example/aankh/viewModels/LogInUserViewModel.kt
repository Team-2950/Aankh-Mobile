package com.example.aankh.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aankh.dataModels.UserModel
import com.example.aankh.repository.logInSystem.LogInUserRepository


class LogInUserViewModel : ViewModel() {
    private lateinit var loginUserViewModel: MutableLiveData<Boolean>
    fun getLogInUserLiveDataViewModel(user: UserModel): MutableLiveData<Boolean> {
        loginUserViewModel = LogInUserRepository.getLogInUserLiveData(user)
        return loginUserViewModel
    }

}