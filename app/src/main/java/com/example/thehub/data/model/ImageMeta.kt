package com.example.thehub.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageMeta(
    @SerializedName("width") val width: Int? = 0,
    @SerializedName("height") val height: Int? = 0
) : Parcelable