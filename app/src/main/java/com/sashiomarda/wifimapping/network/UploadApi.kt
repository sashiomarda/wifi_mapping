package com.sashiomarda.wifimapping.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {
    @Multipart
    @POST("/upload")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>,
        @Part("grid_x") gridX: RequestBody,
        @Part("grid_y") gridY: RequestBody,
        @Part("layer") layer: RequestBody
    ): Response<ResponseBody>
}
