package com.example.thehub.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductImage(
    @SerializedName("path") val path: String,

    @SerializedName("name") val name: String? = null,


    @SerializedName("type") val type: String? = null,
    @SerializedName("access") val access: String? = null,
    @SerializedName("meta") val meta: ImageMeta? = null,

    @SerializedName("mime") val mime: String? = null,
    @SerializedName("size") val size: Int? = 0,


    @SerializedName("url") val url: String? = null
) : Parcelable