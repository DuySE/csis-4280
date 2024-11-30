package com.example.wms.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.models.Product
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.Locale

class ItemRecyclerViewAdapter(
    private var productList: List<Product>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ProductViewHolder>() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.getReference()

    fun setFilteredList(filteredList: MutableList<Product>) {
        productList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.textViewName.text = productList[position].name
        holder.textViewDescription.text =
            String.format("Description: %s", productList[position].description)
        holder.textViewPrice.text =
            String.format(Locale.US, "Price: $%,.2f", productList[position].price)
        val img = storageReference.child("ProductImg/" + productList[position].imgName)
        img.getDownloadUrl().addOnSuccessListener { uri: Uri? ->
            Picasso.get().load(uri).into(holder.imageView)
        }
        holder.textViewCategory.text = String.format("Category: %s", productList[position].category)
        holder.textViewQuantity.text =
            String.format(Locale.US, "Quantity: %d", productList[position].quantity)
        holder.soldOutLabel.visibility =
            if (productList[position].quantity == 0) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.imgViewRecyclerImg)
        val textViewName: TextView = view.findViewById(R.id.txtViewRecyclerName)
        val textViewDescription: TextView = view.findViewById(R.id.txtViewRecyclerDescription)
        val textViewPrice: TextView = view.findViewById(R.id.txtViewRecyclerPrice)
        val textViewCategory: TextView = view.findViewById(R.id.txtViewRecyclerCategory)
        val textViewQuantity: TextView = view.findViewById(R.id.txtViewRecyclerQuantity)
        val soldOutLabel: View = view.findViewById(R.id.soldOutLabel)

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