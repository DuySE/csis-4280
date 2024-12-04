package com.example.wms.apis

import android.content.Context
import com.example.wms.models.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionRepository(context: Context) {

    private val apiService = RetrofitClient.getInstance(context)

    // Add new transaction item
    fun addTransaction(
        transaction: Transaction,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.addTransaction(transaction)
            .enqueue(object : Callback<Transaction> {
                override fun onResponse(
                    call: Call<Transaction>,
                    response: Response<Transaction>
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Error: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Transaction>, t: Throwable) {
                }
            })
    }

    // Get all transactions by date
    fun getTransactionsByDate(
        date: String,
        onSuccess: (List<Transaction>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getTransactionsByDate(date).enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(
                call: Call<List<Transaction>>,
                response: Response<List<Transaction>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }
}