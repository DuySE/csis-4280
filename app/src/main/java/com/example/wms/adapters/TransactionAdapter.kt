package com.example.wms.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.models.Transaction
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.Locale

class TransactionAdapter(
    private var transactionList: MutableList<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.getReference()

    fun setFilteredList(filteredList: MutableList<Transaction>) {
        transactionList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val img = storageReference.child("ProductImg/" + transactionList[position].image)
        img.getDownloadUrl().addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.imageView)
        }
        holder.textViewName.text = transactionList[position].name
        holder.textViewPrice.text =
            String.format(Locale.US, "$%,.2f", transactionList[position].price)
    }

    override fun getItemCount(): Int = transactionList.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.imgViewRecyclerImg)
        val textViewName: TextView = view.findViewById(R.id.productImage)
        val textViewPrice: TextView = view.findViewById(R.id.txtViewTransactionPrice)
    }
}