package com.example.wms

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("_id")
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val quantity: Int,
    val imgName: String
) : Parcelable