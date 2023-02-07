package com.example.aankh.repository.logInSystem

import androidx.lifecycle.MutableLiveData
import com.example.aankh.dataModels.UserModel
import com.example.aankh.utils.PasswordEncryption.getEncryptedPass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogInUserRepository {
    companion object {
        fun getLogInUserLiveData(user: UserModel): MutableLiveData<Boolean> {
            val logInUserLiveData = MutableLiveData<Boolean>()
            val database = FirebaseDatabase.getInstance();
            val databaseReference = database.reference.child("users")
            CoroutineScope(Dispatchers.IO).launch {
                val inputHash: String = getEncryptedPass(user.password)
                databaseReference.child(user.id)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val hashFromDatabase = dataSnapshot.value.toString()
                            if (inputHash == hashFromDatabase) {
                                logInUserLiveData.postValue(true)
                            } else {
                                logInUserLiveData.postValue(false)
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            logInUserLiveData.postValue(false)
                        }
                    })
            }
            return logInUserLiveData
        }
    }
}