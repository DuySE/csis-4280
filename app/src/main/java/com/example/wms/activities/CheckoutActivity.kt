package com.example.wms.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wms.R
import com.example.wms.adapters.OrderAdapter
import com.example.wms.apis.ProductRepository
import com.example.wms.apis.TransactionRepository
import com.example.wms.databinding.ActivityCheckoutBinding
import com.example.wms.helpers.StoredCartHelper
import com.example.wms.models.Cart
import com.example.wms.models.Product
import com.example.wms.models.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : DrawerActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var cart: Cart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Checkout")

        cart = StoredCartHelper.get(this)
        if (cart?.productList?.isNotEmpty() == true) {
            val adapter = OrderAdapter(cart!!.productList, { updatedProductList ->
                updateTotalPrice(updatedProductList)
            }, this)
            binding.recyclerViewOrders.adapter = adapter
            binding.recyclerViewOrders.layoutManager = LinearLayoutManager(this)
            updateTotalPrice(cart!!.productList)
            binding.txtViewCartEmpty.visibility = View.GONE
        } else {
            // Show empty cart message
            binding.txtViewCartEmpty.text = getString(R.string.txtCartEmpty)
            binding.txtViewCartEmpty.visibility = View.VISIBLE
            // Hide order button
            binding.btnConfirmOrder.visibility = View.GONE
        }

        binding.btnConfirmOrder.setOnClickListener {
            val productRepository = ProductRepository(this)
            val transactionRepository = TransactionRepository(this)

            if (cart != null && cart!!.productList.isNotEmpty()) {
                cart!!.productList.forEach { item ->
                    productRepository.getProduct(item.id.toString(),
                        onSuccess = { product ->
                            product.let {
                                if (it.quantity > item.quantity) {
                                    val updateQty = it.quantity - item.quantity
                                    val updatedProduct = Product(
                                        null, it.name, it.description, it.price,
                                        it.category, updateQty, it.imgName
                                    )

                                    productRepository.updateProduct(
                                        product.id.toString(),
                                        updatedProduct,
                                        onSuccess = {
                                            Toast.makeText(
                                                this,
                                                "Product updated.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = {
                                            Toast.makeText(
                                                this,
                                                "Error updating product.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                    // Add product to transaction list
                                    val formatter =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    val date = Date()
                                    val transactionDate: String = formatter.format(date)
                                    val newTransaction = Transaction(
                                        null,
                                        it.imgName,
                                        it.name,
                                        it.price,
                                        transactionDate
                                    )
                                    transactionRepository.addTransaction(
                                        newTransaction,
                                        transactionDate,
                                        onSuccess = {
                                            Toast.makeText(
                                                this,
                                                "Transaction added.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }, onError = { error ->
                                            Toast.makeText(
                                                this,
                                                error,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                }
                            }
                        }, onError = {
                            Toast.makeText(this, "Error updating database.", Toast.LENGTH_SHORT)
                                .show()
                        })
                }
            }
        }
    }

    private fun updateTotalPrice(productList: List<Product>) {
        var subTotal = 0.0
        for (product in productList) subTotal += product.price * product.quantity
        val tax = subTotal * 0.05
        val total = subTotal + tax
        binding.tvSubTotal.text = String.format(Locale.US, "Subtotal: $%,.2f", total)
        binding.tvTax.text = String.format(Locale.US, "Tax: $%,.2f", tax)
        binding.tvTotal.text = String.format(Locale.US, "Total: $%,.2f", total)
    }
}