package com.example.jetpack_compose_call_api.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitInstance {

    private const val BASE_URL = "https://dummyjson.com/"

    // Tạo logging interceptor để xem log request/response trong Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            // Lưu ý: Chúng ta không thêm ConverterFactory ở đây vì chúng ta
            // muốn xử lý ResponseBody một cách thủ công.
            .build()
            .create(ApiService::class.java)
    }
}