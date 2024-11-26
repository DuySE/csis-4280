package com.example.wms

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.example.wms.api.ProductRepository
import com.example.wms.databinding.ActivityProductInfoBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale

class ProductInfoActivity : DrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productInfoBinding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(productInfoBinding.getRoot())
        allocateActivityTitle("Product Information")

        // Initialize product repository with context
        val repository: ProductRepository = ProductRepository(this)

        val imgView = productInfoBinding.imgViewProdInfo
        val txtViewName = productInfoBinding.txtViewProdNameInfo
        val txtViewDescription = productInfoBinding.txtViewProdDescriptionInfo
        val txtViewPrice = productInfoBinding.txtViewProdPriceInfo
        val txtViewCategory = productInfoBinding.txtViewProdCategoryInfo
        val txtViewQuantity = productInfoBinding.txtViewProdQuantityInfo

        val bundleIn = intent.extras
        // will be the id passed from MainActivity
        repository.getProduct(bundleIn?.getInt("ID").toString(), onSuccess = { product ->
            txtViewName.text = product.name
            txtViewDescription.text = product.description
            txtViewPrice.text = String.format(Locale.US, "%,.2f", product.price.toString())
            txtViewCategory.text = product.category
            txtViewQuantity.text = String.format(Locale.US, "%d", product.quantity.toString())

            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.getReference()
            val img = storageReference.child("ProductImg/" + product.imgName)
            img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                Picasso.get().load(uri).into(imgView)
            })
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })
    }
}