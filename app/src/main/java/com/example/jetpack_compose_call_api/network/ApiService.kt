package com.example.jetpack_compose_call_api.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    // API trả về một JSON object chứa một mảng 'products'
    @GET("products")
    suspend fun getProducts(): Response<ResponseBody>
}