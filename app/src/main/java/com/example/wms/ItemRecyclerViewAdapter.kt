package com.example.wms

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.ItemRecyclerViewAdapter.ProductViewHolder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.Locale

class ItemRecyclerViewAdapter(
    private var productList: List<Product>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ProductViewHolder>() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.getReference()

    fun setFilteredList(filteredList: MutableList<Product>) {
        productList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.textViewName.text = productList[position].name
        holder.textViewPrice.text =
            String.format(Locale.US, "%,.2f", productList[position].price)
        val img = storageReference.child("ProductImg/" + productList[position].imgName)
        img.getDownloadUrl().addOnSuccessListener { uri: Uri? ->
            Picasso.get().load(uri).into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.imgViewRecyclerImg)
        val textViewName: TextView = view.findViewById(R.id.txtViewRecyclerName)
        val textViewPrice: TextView = view.findViewById(R.id.txtViewRecyclerPrice)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onProductItemClick(position)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onProductItemClick(i: Int)
    }
}