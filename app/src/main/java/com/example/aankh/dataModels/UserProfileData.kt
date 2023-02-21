package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class UserProfileData(

    @SerializedName("area") var area: String,
    @SerializedName("bloodgroup") var bloodgroup: String,
    @SerializedName("dateofbirth") var dateofbirth: String,
    @SerializedName("designation") var designation: String,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: String
)
