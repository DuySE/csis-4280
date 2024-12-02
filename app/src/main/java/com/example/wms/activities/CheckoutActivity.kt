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
import com.example.wms.R
import com.example.wms.apis.ProductRepository
import com.example.wms.models.Product
import com.example.wms.utils.StoredCartHelper

class CheckoutActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtViewResult = findViewById<TextView>(R.id.txtViewCheckout)

        val cartItems = StoredCartHelper.get(this)

        if (cartItems.any()) {
            for (item in cartItems) {
                txtViewResult.text = "ID:" + item.productId + " Quantity:" + item.quantity.toString() + "\n"
            }
        }

        val btnUpdate = findViewById<Button>(R.id.btnUpdateDB)
        btnUpdate.setOnClickListener{
            val productRepository = ProductRepository(this)
            for (item in cartItems) {
                var product = productRepository.getProduct(item.productId.toString(), onSuccess = { product ->
                    if (product.quantity > item.quantity) {
                        val updateQty = product.quantity - item.quantity
                        val updatedProduct = Product (
                            null, product.name, product.description, product.price, product.category, updateQty, product.imgName)

                        productRepository.updateProduct(product.id.toString(), updatedProduct, onSuccess = { product ->
                            Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show()
                        } , onError = { error ->
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        })
                    }
                }, onError = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                })
            }
        }

    }
}