package com.example.wms.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.apis.ProductRepository
import com.example.wms.databinding.OrderItemBinding
import com.example.wms.models.Cart
import com.example.wms.models.Product
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class OrderAdapter (
    private val productList: MutableList<Product>,
    private val context: Context
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.txtViewRecyclerPrice.text = product.price.toString()
        holder.binding.txtViewRecyclerName.text = product.name
        holder.binding.txtViewRecyclerQuantity.text = product.quantity.toString()
        holder.binding.txtViewRecyclerDescription.text = product.description
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.getReference()
        val img = storageReference.child("ProductImg/" + product.imgName)
        img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
            Picasso.get().load(uri).into(holder.binding.itemImage)
        })
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class OrderViewHolder(private val _binding: OrderItemBinding): RecyclerView.ViewHolder(_binding.root) {
        public val binding = _binding
        private val productRepository = ProductRepository(context)
        private var maxQty = 0
        private var currentQty = 0
        private var strCurrentQty = ""

        init {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                val productId = productList[adapterPosition].id!!.toString()
                productRepository.getProduct(productId,
                    onSuccess = {product -> maxQty = product.quantity},
                    onError = {error -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show()}
                )

                binding.btnDecreaseQuantity.setOnClickListener{
                    strCurrentQty = binding.txtViewRecyclerQuantity.text.toString()
                    currentQty = if (strCurrentQty.isNotEmpty()) strCurrentQty.toInt() else 0
                    if (currentQty > 0) {
                        currentQty--
                        binding.txtViewRecyclerQuantity.text = currentQty.toString()
                        updateProductQuantity(productId, currentQty)
                    }
                }

                binding.btnIncreaseQuantity.setOnClickListener {
                    strCurrentQty = binding.txtViewRecyclerQuantity.text.toString()
                    currentQty = if (strCurrentQty.isNotEmpty()) strCurrentQty.toInt() else 0
                    if (currentQty < maxQty) {
                        currentQty++
                        binding.txtViewRecyclerQuantity.text = currentQty.toString()
                        updateProductQuantity(productId, currentQty)
                    }
                }
            }
        }
    }



    fun updateProductQuantity(productId: String, newQty: Int) {
        productList.forEachIndexed { index, product ->
            if (product.id == productId) {
                productList[index] = product.copy(quantity = newQty)
                return@forEachIndexed
            }
        }
    }
}