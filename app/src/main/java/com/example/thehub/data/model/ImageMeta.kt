package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class ImageMeta(
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)
