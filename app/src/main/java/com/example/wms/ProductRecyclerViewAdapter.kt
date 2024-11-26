package com.example.wms

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class ProductRecyclerViewAdapter(
    products: List<Product>,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ProductRecyclerViewAdapter.MyViewHolder?>() {
    var products: List<Product>
    var onItemClickListener: OnItemClickListener

    init {
        this.products = products
        this.onItemClickListener = onItemClickListener
    }

    fun setFilteredList(filteredList: MutableList<Product>) {
        products = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_product_recycler_view, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtViewName.text = products[position].name
        holder.txtViewDescription.text = products[position].description
        holder.txtViewPrice.text =
            String.format(Locale.US, "%,.2f", products[position].price.toString())
        holder.txtViewCategory.text = products[position].category
        holder.txtViewQuantity.text =
            String.format(Locale.US, "%d", products[position].quantity.toString())
        val imgName = products[position].imgName
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val storage = FirebaseStorage.getInstance()
                val storageReference = storage.getReference()
                val img = storageReference.child("ProductImg/$imgName")
                img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
                    Picasso.get().load(uri).into(holder.imgView)
                })
            }
        }

        val timer = Timer()
        timer.schedule(timerTask, 2000)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgView: ImageView = itemView.findViewById<ImageView>(R.id.imgViewRecyclerImg)
        var imgViewEdit: ImageView = itemView.findViewById<ImageView>(R.id.imgViewRecyclerEdit)
        var txtViewName: TextView = itemView.findViewById<TextView>(R.id.txtViewRecyclerName)
        var txtViewDescription: TextView =
            itemView.findViewById<TextView>(R.id.txtViewRecyclerDescription)
        var txtViewPrice: TextView = itemView.findViewById<TextView>(R.id.txtViewRecyclerPrice)
        var txtViewCategory: TextView =
            itemView.findViewById<TextView>(R.id.txtViewRecyclerCategory)
        var txtViewQuantity: TextView =
            itemView.findViewById<TextView>(R.id.txtViewRecyclerQuantity)

        init {
            imgViewEdit.setOnClickListener {
                onItemClickListener.onItemClick(
                    getAdapterPosition()
                )
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(i: Int)
    }
}
