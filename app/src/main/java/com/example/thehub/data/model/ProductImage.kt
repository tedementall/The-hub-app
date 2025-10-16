package com.example.thehub.data.model

import com.google.gson.annotations.SerializedName

data class ProductImage(
    @SerializedName("access") val access: String,
    @SerializedName("path") val path: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("size") val size: Long,
    @SerializedName("mime") val mime: String,
    @SerializedName("meta") val meta: ImageMeta
)
