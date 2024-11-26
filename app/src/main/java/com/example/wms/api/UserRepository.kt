package com.example.wms.api

import android.content.Context
import com.example.wms.LoginRequest
import com.example.wms.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(context: Context) {

    private val apiService = RetrofitClient.getInstance(context)

    // Add a new user
    fun addUser(
        user: User,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.addUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Get all users
    fun getAllUsers(
        onSuccess: (List<User>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Get a user by username
    fun getUser(
        username: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getUser(username).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Login with username and password
    fun login(
        loginRequest: LoginRequest,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.login(loginRequest).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Update a user by username
    fun updateUser(
        username: String,
        updatedUser: User,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.updateUser(username, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    // Delete a user
    fun deleteUser(
        username: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.deleteUser(username).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                onError(t.message ?: "Unknown error occurred")
            }
        })
    }
}