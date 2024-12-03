package com.example.wms.models

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
    var quantity: Int,
    val imgName: String
) : Parcelable