package com.example.wms.adapters

import android.content.DialogInterface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.models.Product
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class ProductRecyclerViewAdapter(
    private var products: List<Product>,
    private var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ProductRecyclerViewAdapter.MyViewHolder?>() {

    fun setFilteredList(filteredList: MutableList<Product>) {
        products = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_product_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtViewName.text = products[position].name
        holder.txtViewDescription.text =
            String.format("Description: %s", products[position].description)
        holder.txtViewPrice.text =
            String.format(Locale.US, "Price: $%,.2f", products[position].price)
        holder.txtViewCategory.text = String.format("Category: %s", products[position].category)
        holder.txtViewQuantity.text =
            String.format(Locale.US, "Quantity: %d", products[position].quantity)
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

    override fun getItemCount(): Int = products.size

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgView: ImageView = view.findViewById<ImageView>(R.id.imgViewRecyclerImg)
        val imgViewEdit: ImageView = view.findViewById<ImageView>(R.id.btnEditProfile)
        val imgViewDelete: ImageView = view.findViewById<ImageView>(R.id.btnDeleteProfile)
        val txtViewName: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerName)
        val txtViewDescription: TextView =
            view.findViewById<TextView>(R.id.txtViewRecyclerDescription)
        val txtViewPrice: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerPrice)
        val txtViewCategory: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerCategory)
        val txtViewQuantity: TextView = view.findViewById<TextView>(R.id.txtViewRecyclerQuantity)

        init {
            imgViewEdit.setOnClickListener {
                onItemClickListener.onItemEdit(adapterPosition)
            }
            imgViewDelete.setOnClickListener {
                val builder = AlertDialog.Builder(view.context)
                builder.setIcon(R.drawable.ic_launcher_foreground)
                builder.setTitle(R.string.app_name)
                builder.setMessage("Do you want to delete this product?")
                builder.setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        onItemClickListener.onItemDelete(adapterPosition)
                    })
                builder.setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialog: DialogInterface?, _ -> dialog!!.cancel() })
                val alertDialog = builder.create()
                alertDialog.show()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemEdit(i: Int)
        fun onItemDelete(i: Int)
    }
}
