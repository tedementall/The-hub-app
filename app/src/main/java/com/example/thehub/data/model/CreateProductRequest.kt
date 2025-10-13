package com.example.thehub.data.model

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val images: List<ProductImage>
)
