package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class UpdateResponse(
    @SerializedName("status") val status: String
)
