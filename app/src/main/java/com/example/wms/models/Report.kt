package com.example.wms.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Report(
    val date: String,
    val totalRevenue: Double,
    val totalTransactions: Int,
    val profits: Double
) : Parcelable