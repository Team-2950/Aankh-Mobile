package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class CurrentLocationUpdateResponse(
    @SerializedName("status") val status: String
)
