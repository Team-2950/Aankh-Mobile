package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class CheckPointsDataModel(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("description") val description: String
)


