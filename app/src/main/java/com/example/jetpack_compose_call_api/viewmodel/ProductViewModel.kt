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
import kotlinx.coroutines.launch

// ProductViewModel.kt

class ProductViewModel(
    private val repository: ProductRepository = ProductRepository(RetrofitInstance.api)
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        _isLoading.value = true
                        _errorMessage.value = null
                    }
                    is UiState.Success -> {
                        _isLoading.value = false
                        _products.value = state.data
                    }
                    is UiState.Error -> {
                        _isLoading.value = false
                        _errorMessage.value = state.message
                    }
                }
            }
        }
    }

    fun errorMessageShown() {
        _errorMessage.value = null
    }
}