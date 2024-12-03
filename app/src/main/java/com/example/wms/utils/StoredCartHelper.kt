package com.example.wms.utils

import android.content.Context
import com.example.wms.models.Cart
import com.google.gson.Gson

object StoredCartHelper {
    fun save(context: Context, cartItems: List<Cart>?) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (cartItems != null) {
            val json = Gson().toJson(cartItems)
            editor.putString("cartItem", json)
        } else editor.remove("cartItem")
        editor.apply()
    }

    fun get(context: Context): List<Cart> {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("cartItem", "[]")
        return Gson().fromJson(value, Array<Cart>::class.java).toList()
    }

    fun modify(context: Context, cart: Cart) {
        val cartItems = get(context).toMutableList()
        val index = cartItems.indexOfFirst { it.productId == cart.productId }
        if (index != -1) cartItems[index] = cart
        else cartItems.add(cart)
        save(context, cartItems)
    }

    fun clear(context: Context) {
        val sharedPreferences = context.getSharedPreferences("application", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}