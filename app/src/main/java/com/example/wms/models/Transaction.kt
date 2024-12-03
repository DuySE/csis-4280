package com.example.wms.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val image: String,
    val name: String,
    val price: Double,
    val date: String
) : Parcelable