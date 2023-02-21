package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class VerificationImageDataModel(
    @SerializedName("image1") var image1: String,
    @SerializedName("image2") var image2: String
)