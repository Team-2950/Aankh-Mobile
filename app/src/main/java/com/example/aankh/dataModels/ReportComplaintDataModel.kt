package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class ReportComplaintDataModel(
    @SerializedName("date") var date: String,
    @SerializedName("time") var time: String,
    @SerializedName("reportingofficername") var reportingofficername: String,
    @SerializedName("residenceofcomplainant") var residenceofcomplainant: String,
    @SerializedName("description") var description: String
)
