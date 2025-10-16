package com.example.thehub.data.remote

import com.example.thehub.data.model.ProductImage
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {
    @Multipart
    @POST("upload/image")
    suspend fun uploadImages(
        @Part parts: List<MultipartBody.Part> // cada part con nombre "content[]"
    ): List<ProductImage>
}
