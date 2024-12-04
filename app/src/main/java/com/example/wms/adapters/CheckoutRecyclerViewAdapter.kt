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
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class CheckoutRecyclerViewAdapter(
    private var products: List<Product>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CheckoutRecyclerViewAdapter.MyViewHolder?>() {

    fun setFilteredList(filteredList: MutableList<Product>) {
        products = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtViewName.text = products[position].name
        holder.txtViewDescription.text =
            String.format("Description: %s", products[position].description)
        holder.txtViewPrice.text =
            String.format(Locale.US, "Price: $%,.2f", products[position].price)
        holder.txtViewQuantity.text =
            String.format(Locale.US, "%d", products[position].quantity)
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

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById<ImageView>(R.id.imgViewRecyclerImg)
        val btnInc: ImageView = view.findViewById<ImageView>(R.id.btnIncreaseQty)
        val btnDec: ImageView = view.findViewById<ImageView>(R.id.btnDecreaseQty)
        val txtViewName: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerName)
        val txtViewDescription: TextView =
            view.findViewById<TextView>(R.id.txtViewRecyclerDescription)
        val txtViewPrice: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerPrice)
        val txtViewQuantity: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerQuantity)

        init {
            btnInc.setOnClickListener {
                onItemClickListener.onQuantityIncrease(adapterPosition)
            }
            btnDec.setOnClickListener {
                onItemClickListener.onQuantityDecrease(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onQuantityIncrease(i: Int)
        fun onQuantityDecrease(i: Int)
    }
}