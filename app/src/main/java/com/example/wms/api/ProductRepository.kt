package com.example.wms.api

import android.content.Context
import com.example.wms.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository(context: Context) {

    private val apiService = RetrofitClient.getInstance(context)

    // Create a new product
    fun createProduct(
        product: Product,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.createProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Get all products
    fun getAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Get a product by id
    fun getProduct(
        id: String,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getProduct(id).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Update a product by id
    fun updateProduct(
        id: String,
        updatedProduct: Product,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.updateProduct(id, updatedProduct).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Delete a product
    fun deleteProduct(
        id: String,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.deleteProduct(id).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }
}