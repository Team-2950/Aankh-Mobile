package com.example.aankh.dataModels

import com.google.gson.annotations.SerializedName

data class ImageMatchingResultDataModel(

    @SerializedName("realness_score2") var realnessScore2: Double,
    @SerializedName("similarity_score") var similarityScore: ArrayList<Int> = arrayListOf()
)
