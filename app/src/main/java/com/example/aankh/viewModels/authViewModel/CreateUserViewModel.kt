package com.example.aankh.viewModels.authViewModel

import android.util.MutableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aankh.dataModels.UserModel
import com.example.aankh.repository.logInSystem.CreateUserRepository

class CreateUserViewModel() : ViewModel() {
    private lateinit var createUserViewModel: MutableLiveData<Boolean>
    fun getCreateUserLiveDataViewModel(user: UserModel): MutableLiveData<Boolean> {
        createUserViewModel = CreateUserRepository.getCreateUserLiveData(user)
        return createUserViewModel
    }

}