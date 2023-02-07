package com.example.aankh.repository.logInSystem

import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.UserModel
import com.example.aankh.utils.PasswordEncryption.getEncryptedPass
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

class CreateUserRepository {

    companion object {
        fun getCreateUserLiveData(user: UserModel): MutableLiveData<Boolean> {
            val createUserLiveData = MutableLiveData<Boolean>()
            val database = FirebaseDatabase.getInstance();
            val databaseReference = database.reference.child("users")
            CoroutineScope(Dispatchers.IO).launch {
                val encryptedPassword = getEncryptedPass(user.password)
                databaseReference.child(user.id).setValue(encryptedPassword)
                    .addOnSuccessListener {
                        createUserLiveData.postValue(
                            true
                        )
                    }.addOnFailureListener { createUserLiveData.postValue(false) }

            }
            return createUserLiveData
        }


    }


}