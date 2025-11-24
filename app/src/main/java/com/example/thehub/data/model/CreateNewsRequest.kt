package com.example.thehub.data.model

data class CreateNewsRequest(
    val title: String,
    val body: String,
    val cover: List<ProductImage>
)