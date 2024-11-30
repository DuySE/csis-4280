package com.example.wms.utils

import android.content.Context
import java.util.Locale

object StoredDataHelper {
    // Store data as key-value pair to SharedPreferences
    fun save(context: Context, value: String?) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", value)
        editor.apply()
    }

    // Get value by key from SharedPreferences
    fun get(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val value: String = sharedPreferences.getString("username", "")!!
        return value.lowercase(Locale.getDefault())
    }

    // Clear values in SharedPreferences
    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
