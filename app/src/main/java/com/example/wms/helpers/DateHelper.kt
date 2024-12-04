package com.example.wms.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {
    fun getCurrentDate(): String {
        val locale: Locale = Locale.getDefault()
        val formatter = SimpleDateFormat("yyyy-MM-dd", locale)
        val date = Date()
        val transactionDate: String = formatter.format(date)
        return transactionDate
    }
}