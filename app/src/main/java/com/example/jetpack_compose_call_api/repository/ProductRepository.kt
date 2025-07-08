package com.example.jetpack_compose_call_api.repository

import android.util.Log
import com.example.jetpack_compose_call_api.model.Product
import com.example.jetpack_compose_call_api.network.ApiService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProductRepository(private val apiService: ApiService) {

    fun getProducts(): Flow<UiState<List<Product>>> = flow {
        // Bắt đầu: phát ra trạng thái Loading
        emit(UiState.Loading)

        try {
            val response = apiService.getProducts()

            if (response.isSuccessful) {
                // Lấy phần body của response
                val responseBody = response.body()
                if (responseBody != null) {
                    // Chuyển ResponseBody thành String. Lưu ý: .string() chỉ có thể gọi 1 lần!
                    val jsonString = responseBody.string()

                    // Dùng Gson để parse chuỗi JSON
                    val gson = Gson()
                    // API dummyjson trả về object {"products": [...], "total": ..., ...}
                    // nên chúng ta cần lấy ra mảng "products"
                    val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                    val productJsonArray = jsonObject.getAsJsonArray("products")
                    Log.d("frank", "getProducts: $productJsonArray")

                    // Chuyển đổi JsonArray thành List<Product>
                    val productListType = object : TypeToken<List<Product>>() {}.type
                    val products: List<Product> = gson.fromJson(productJsonArray, productListType)
                    Log.d("frank", "getProducts: $products")

                    // Thành công: phát ra dữ liệu
                    emit(UiState.Success(products))
                } else {
                    emit(UiState.Error("Response body is null"))
                }
            } else {
                // Lỗi từ server (vd: 404, 500)
                emit(UiState.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Lỗi kết nối mạng hoặc lỗi parse JSON
            emit(UiState.Error("Exception: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO) // Thực hiện các thao tác mạng trên IO thread
}