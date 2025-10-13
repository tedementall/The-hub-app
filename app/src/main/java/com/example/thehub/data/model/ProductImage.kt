package com.example.thehub.data.model

data class ProductImage(
    val access: String?,
    val path: String?,
    val name: String?,
    val type: String?,
    val size: Int?,
    val mime: String?,
    val meta: ImageMeta? // si usas ImageMeta
)
