package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class CurrentLocationDataModel(
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("latitude") var latitude: Double
)
