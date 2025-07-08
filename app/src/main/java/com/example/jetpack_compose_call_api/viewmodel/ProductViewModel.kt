package com.example.jetpack_compose_call_api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpack_compose_call_api.model.Product
import com.example.jetpack_compose_call_api.network.RetrofitInstance
import com.example.jetpack_compose_call_api.repository.ProductRepository
import com.example.jetpack_compose_call_api.repository.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProductViewModel(
    // Trong thực tế, bạn sẽ inject repository này bằng Hilt/Dagger
    private val repository: ProductRepository = ProductRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Product>>> = _uiState.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts()
                .catch { e ->
                    // Bắt các lỗi không mong muốn từ Flow
                    _uiState.value = UiState.Error(e.message.toString())
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }
}