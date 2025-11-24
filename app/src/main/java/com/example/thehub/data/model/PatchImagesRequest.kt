package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class PatchImagesRequest(
    @SerializedName("image_url") val imageUrl: List<String>
)