package com.example.wms.apis

import com.example.wms.models.LoginRequest
import com.example.wms.models.Product
import com.example.wms.models.Transaction
import com.example.wms.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Route to add a new user
    @POST("/users")
    fun addUser(@Body user: User): Call<User>

    // Route to get all users
    @GET("/users")
    fun getAllUsers(): Call<List<User>>

    // Route to login with username and password
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<User>

    // Route to get a user by username
    @GET("/users/{username}")
    fun getUser(@Path("username") username: String): Call<User>

    // Route to update a user
    @PUT("/users/{username}")
    fun updateUser(@Path("username") username: String, @Body updatedUser: User): Call<User>

    // Route to delete a user
    @DELETE("/users/{username}")
    fun deleteUser(@Path("username") username: String): Call<User>

    // Route to create a new product
    @POST("/products")
    fun createProduct(@Body product: Product): Call<Product>

    // Route to get all products
    @GET("/products")
    fun getAllProducts(): Call<List<Product>>

    // Route to get a product by id
    @GET("/products/{id}")
    fun getProduct(@Path("id") id: String): Call<Product>

    // Route to update a product
    @PUT("/products/{id}")
    fun updateProduct(@Path("id") id: String, @Body updatedProduct: Product): Call<Product>

    // Route to delete a product
    @DELETE("/products/{id}")
    fun deleteProduct(@Path("id") id: String): Call<Product>

    // Route to get transactions by date
    @GET("/transactions")
    fun getTransaction(
        @Query("date") date: String
    ): Call<List<Transaction>>

    // Route to add a new transaction item
    @POST("/transactions")
    fun addTransaction(
        @Body transaction: Transaction
    ): Call<Transaction>
}