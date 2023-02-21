package com.example.aankh.viewModels.uiViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aankh.dataModels.VerificationImageDataModel
import com.example.aankh.repository.remoteDataSource.machineLearning.VerificationRepository
import kotlinx.coroutines.launch

class VerificationViewModel : ViewModel() {

    private val verificationRepo = VerificationRepository()


    fun compareImage(userid: String, imageSet: VerificationImageDataModel) {
        viewModelScope.launch {
            verificationRepo.compareImage(userid, imageSet)
        }
    }

    fun getResult() = verificationRepo.getResult()


}