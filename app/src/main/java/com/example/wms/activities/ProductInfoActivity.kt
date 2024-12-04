package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.ActivityProductInfoBinding
import com.example.wms.helpers.StoredCartHelper
import com.example.wms.models.Cart
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

        val bundleIn = intent.extras
        // will be the id passed from MainActivity
        repository.getProduct(bundleIn?.getString("ID").toString(), onSuccess = { product ->
            productId = product.id
            txtViewName.text = product.name
            txtViewDescription.text = product.description
            txtViewPrice.text = String.format(Locale.US, "Price: $%,.2f", product.price)
            txtViewCategory.text = String.format("Category: %s", product.category)

            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.getReference()
            val img = storageReference.child("ProductImg/" + product.imgName)
            img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri ->
                Picasso.get().load(uri).into(imgView)
            })
            // Disable "Add To Cart" button if product is out of stock
            productInfoBinding.btnAddCart.isEnabled = product.quantity != 0
        }, onError = { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show() })

        productInfoBinding.btnAddCart.setOnClickListener {
            repository.getProduct(productId.toString(),
                onSuccess = { product ->
                    val cart: Cart = StoredCartHelper.get(this) ?: Cart(mutableListOf())

                    val existingProductIndex = cart.productList.indexOfFirst { it.id == product.id }
                    if (existingProductIndex != -1) {
                        cart.productList[existingProductIndex] = product
                    } else {
                        product.quantity = 1
                        cart.productList.add(product)
                    }

                    StoredCartHelper.save(this, cart)
                    Toast.makeText(this, "Product added to cart.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                },
                onError = { error -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show() }
            )
        }
    }
}