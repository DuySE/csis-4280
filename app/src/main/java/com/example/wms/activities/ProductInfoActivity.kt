package com.example.wms.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityProductInfoBinding
import com.example.wms.models.Cart
import com.example.wms.utils.StoredCartHelper
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale

class ProductInfoActivity : DrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productInfoBinding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(productInfoBinding.root)
        allocateActivityTitle("Product Information")

        // Initialize product repository with context
        val repository: ProductRepository = ProductRepository(this)

        var productId: String? = null
        val imgView = productInfoBinding.imgViewProdInfo
        val txtViewName = productInfoBinding.txtViewProdNameInfo
        val txtViewDescription = productInfoBinding.txtViewProdDescriptionInfo
        val txtViewPrice = productInfoBinding.txtViewProdPriceInfo
        val txtViewCategory = productInfoBinding.txtViewProdCategoryInfo
        //val txtViewQuantity = productInfoBinding.txtViewProdQuantityInfo
        val numPickerQuantity = productInfoBinding.numPickerQuantity

        val bundleIn = intent.extras
        // will be the id passed from MainActivity
        repository.getProduct(bundleIn?.getString("ID").toString(), onSuccess = { product ->
            productId = product.id
            txtViewName.text = product.name
            txtViewDescription.text = product.description
            txtViewPrice.text = String.format(Locale.US, "Price: $%,.2f", product.price)
            txtViewCategory.text = String.format("Category: %s", product.category)
            //txtViewQuantity.text = String.format(Locale.US, "%d in stock", product.quantity)
            numPickerQuantity.setMinValue(0)
            numPickerQuantity.setMaxValue(product.quantity)
            numPickerQuantity.setValue(0)

            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.getReference()
            val img = storageReference.child("ProductImg/" + product.imgName)
            img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                Picasso.get().load(uri).into(imgView)
            })
        }, onError = { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show() })

        productInfoBinding.btnAddCart.setOnClickListener{
            val selectedQuantity = numPickerQuantity.value;
            var cart: Cart?
            if (productId != null && selectedQuantity > 0) {
                cart = Cart(productId!!, selectedQuantity)
                val cartItems = StoredCartHelper.get(this)
                if (cartItems.isEmpty()) {
                    val updatedCartItems = mutableListOf(cart)
                    StoredCartHelper.save(this, updatedCartItems)
                } else {
                    val updatedCartItems = cartItems.toMutableList()
                    updatedCartItems.add(cart)
                    StoredCartHelper.save(this, updatedCartItems)
                }
                Toast.makeText(this, "Success to add item to cart", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "Failed to add item to a cart.", Toast.LENGTH_LONG).show()
            }
        }

        productInfoBinding.btnCheckout.setOnClickListener{
            startActivity((Intent(this, CheckoutActivity::class.java)))
        }
    }
}