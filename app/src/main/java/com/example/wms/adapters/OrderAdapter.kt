package com.example.wms.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.helpers.StoredCartHelper
import com.example.wms.models.Cart
import com.example.wms.models.Product
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale

class OrderAdapter(
    private var productList: MutableList<Product>,
    private val onQuantityChange: (List<Product>) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val product = productList[position]

        // Set product details in the ViewHolder
        holder.txtViewName.text = product.name
        holder.txtViewDescription.text = product.description
        holder.txtViewPrice.text = String.format(Locale.US, "$%,.2f", product.price)

        // Initialize quantity to 0
        holder.txtViewQuantity.text = "1"

        // Load image from Firebase Storage
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.getReference()
        val img = storageReference.child("ProductImg/" + product.imgName)
        img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
            Picasso.get().load(uri).into(holder.imgView)
        })

        // Increase quantity
        holder.btnIncreaseQty.setOnClickListener {
            val currentQty = holder.txtViewQuantity.text.toString().toIntOrNull() ?: 0
            val newQty = currentQty + 1
            holder.txtViewQuantity.text = String.format(Locale.US, "%d", newQty)
            updateProductQuantity(product.id!!, newQty)
            onQuantityChange(productList) // Notify activity
        }

        // Decrease quantity
        holder.btnDecreaseQty.setOnClickListener {
            val currentQty = holder.txtViewQuantity.text.toString().toIntOrNull() ?: 0
            if (currentQty > 0) {
                val newQty = currentQty - 1
                holder.txtViewQuantity.text = String.format(Locale.US, "%d", newQty)
                updateProductQuantity(product.id!!, newQty)
                onQuantityChange(productList) // Notify activity
            }
        }
    }

    override fun getItemCount(): Int = productList.size

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById<ImageView>(R.id.productImage)
        val txtViewName: TextView = view.findViewById<TextView>(R.id.productName)
        val txtViewDescription: TextView =
            view.findViewById<TextView>(R.id.productDescription)
        val txtViewPrice: TextView = view.findViewById<TextView>(R.id.productPrice)
        val txtViewQuantity: TextView = view.findViewById<TextView>(R.id.productQty)
        val btnIncreaseQty: Button = view.findViewById<Button>(R.id.btnIncreaseQty)
        val btnDecreaseQty: Button = view.findViewById<Button>(R.id.btnDecreaseQty)
    }

    private fun updateProductQuantity(productId: String, newQty: Int) {
        val updatedList = productList.map { product ->
            if (product.id == productId) {
                if (newQty == 0) null // Mark for removal
                else product.copy(quantity = newQty)
            } else product
        }.filterNotNull() // Remove null values (marked for deletion)
        productList.clear()
        productList.addAll(updatedList)

        // Save the updated cart
        val updatedCart = Cart(productList)
        StoredCartHelper.save(context, updatedCart)
    }
}