package com.example.wms.utils

import android.content.Context
import com.example.wms.models.Cart
import com.example.wms.models.Product
import com.google.gson.Gson

object StoredCartHelper {
    fun save(context: Context, cart: Cart) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (cart != null) {
            val json = Gson().toJson(cart)
            editor.putString("cart", json)
        } else {
            editor.remove("cart")
        }

        editor.apply()
    }

    fun get(context: Context): Cart? {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("cart", "")
        if (value == null) {
            return null
        }
        return Gson().fromJson(value, Cart::class.java)
    }

    fun modify(context: Context, updatedItem: Product) {
        val cart = get(context)

        val newProductList = cart!!.productList!!.toMutableList()
        newProductList!!.find { it.id == updatedItem.id }?.let {
            newProductList[newProductList.indexOf(it)] = updatedItem
        }

        save(context, cart!!.copy(productList = newProductList))
    }

    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}