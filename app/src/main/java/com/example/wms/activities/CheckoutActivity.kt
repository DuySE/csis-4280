package com.example.wms.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.adapters.CheckoutRecyclerViewAdapter
import com.example.wms.adapters.ItemRecyclerViewAdapter
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityCheckoutBinding
import com.example.wms.models.Product
import com.example.wms.utils.StoredCartHelper

class CheckoutActivity : DrawerActivity(),
    ItemRecyclerViewAdapter.OnItemClickListener {
    private lateinit var checkoutBinding: ActivityCheckoutBinding
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var myAdapter: CheckoutRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkoutBinding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(checkoutBinding.root)
        allocateActivityTitle("Checkout")

        // Set adapter for recycler view
        recyclerViewProduct = checkoutBinding.recyclerViewOrders
        myAdapter = CheckoutRecyclerViewAdapter(StoredCartHelper.get(this), this)
        recyclerViewProduct.adapter = myAdapter
        recyclerViewProduct.layoutManager = GridLayoutManager(this, 2)

        val cartItems = StoredCartHelper.get(this)

        if (cartItems.any()) {
            for (item in cartItems) {
                txtViewResult.text =
                    "ID:" + item.productId + " Quantity:" + item.quantity.toString() + "\n"
            }
        }

        val btnUpdate = findViewById<Button>(R.id.btnUpdateDB)
        btnUpdate.setOnClickListener {
            val productRepository = ProductRepository(this)
            for (item in cartItems) {
                var product =
                    productRepository.getProduct(item.productId.toString(), onSuccess = { product ->
                        if (product.quantity > item.quantity) {
                            val updateQty = product.quantity - item.quantity
                            val updatedProduct = Product(
                                null,
                                product.name,
                                product.description,
                                product.price,
                                product.category,
                                updateQty,
                                product.imgName
                            )

                            productRepository.updateProduct(
                                product.id.toString(),
                                updatedProduct,
                                onSuccess = { product ->
                                    Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onError = { error ->
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