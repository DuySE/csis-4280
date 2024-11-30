package com.example.wms.adapters

import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.wms.R
import com.example.wms.models.Transaction
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class TransactionAdapter(adapterTransactions: MutableList<Transaction?>) : BaseAdapter() {
    var adapterTransactions: MutableList<Transaction?>
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = storage.getReference()

    init {
        this.adapterTransactions = adapterTransactions
    }

    fun setFilteredList(filteredList: MutableList<Transaction?>) {
        this.adapterTransactions = filteredList
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return adapterTransactions.size
    }

    override fun getItem(i: Int): Any? {
        return adapterTransactions[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var v = view
        if (v == null) {
            v = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.layout_transaction, viewGroup, false
            )
        }
        val txtViewTransactionProductName =
            v!!.findViewById<TextView>(R.id.txtViewTransactionProductName)
        val txtViewTransactionDate = v.findViewById<TextView>(R.id.txtViewTransactionDate)
        val productImage = v.findViewById<ImageView?>(R.id.productImage)
        txtViewTransactionProductName.text = adapterTransactions[i]!!.getProductName()
        txtViewTransactionDate.gravity = Gravity.END
        txtViewTransactionDate.text = adapterTransactions[i]!!.getDate()
        val img = storageReference.child(
            "ProductImg/" +
                    adapterTransactions[i]!!.getImageName()
        )
        img.getDownloadUrl().addOnSuccessListener(OnSuccessListener { uri: Uri? ->
            Picasso.get().load(uri).into(productImage)
        })
        return v
    }
}