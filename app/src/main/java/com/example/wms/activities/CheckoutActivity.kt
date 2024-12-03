package com.example.wms.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wms.R
import com.example.wms.adapters.OrderAdapter
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityCheckoutBinding
import com.example.wms.models.Cart
import com.example.wms.models.Product
import com.example.wms.utils.StoredCartHelper

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var cart: Cart? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        cart = StoredCartHelper.get(this)
        if (cart?.productList?.isNotEmpty() == true) {
            val adapter = OrderAdapter(cart!!.productList, this)
            binding.recyclerViewOrders.adapter = adapter
            binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
            updateTotalPrice(cart!!)
        }

        binding.btnConfirmOrder.setOnClickListener {
            val productRepository = ProductRepository(this)

            if (cart != null && cart!!.productList.isNotEmpty()) {
                cart!!.productList.forEach { item ->
                    val product = productRepository.getProduct(item.id.toString(),
                        onSuccess = { product ->
                            product.let {
                                if (it.quantity > item.quantity) {
                                    val updateQty = it.quantity - item.quantity
                                    val updatedProduct = Product(
                                        null, it.name, it.description, it.price,
                                        it.category, updateQty, it.imgName
                                    )

                                    productRepository.updateProduct(
                                        updatedProduct.id.toString(),
                                        updatedProduct,
                                        onSuccess = {
                                            Toast.makeText(
                                                this,
                                                "Product updated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = {
                                            Toast.makeText(
                                                this,
                                                "Error updating product",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                }
                            }
                        }, onError = {
                            Toast.makeText(this, "Error updating database", Toast.LENGTH_SHORT)
                                .show()
                        })
                }
            }
        }
    }

    private fun updateTotalPrice(cart: Cart) {
        var total = 0.0
        for (product in cart.productList) {
            total += product.price * product.quantity
        }
        binding.tvTotal.text = total.toString()
    }
}