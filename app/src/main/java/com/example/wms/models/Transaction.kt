package com.example.wms.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    @SerializedName("_id")
    val id: String? = null,
    val image: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val date: String
) : Parcelable