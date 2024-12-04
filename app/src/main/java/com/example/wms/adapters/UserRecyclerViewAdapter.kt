package com.example.wms.adapters

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.wms.R
import com.example.wms.models.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UserRecyclerViewAdapter(
    private var userList: List<User>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<UserRecyclerViewAdapter.UserViewHolder>() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.getReference()

    fun setFilteredList(filteredList: MutableList<User>) {
        userList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.userName.text = userList[position].username
        holder.userAddress.text = userList[position].address
        holder.userPhone.text = userList[position].phone
        val img = storageReference.child("ProfileImg/${userList[position].profileImg}")
        img.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(holder.imageView)
        }.addOnFailureListener {
            holder.imageView.setImageResource(R.drawable.default_profile_photo)
        }
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.userImage)
        val imgViewEdit: ImageView = view.findViewById<ImageView>(R.id.btnEditProfile)
        val imgViewDelete: ImageView = view.findViewById<ImageView>(R.id.btnDeleteProfile)
        val userName: TextView = view.findViewById(R.id.userName)
        val userAddress: TextView = view.findViewById(R.id.userAddress)
        val userPhone: TextView = view.findViewById(R.id.userPhone)

        init {
            imgViewEdit.setOnClickListener {
                onItemClickListener.onItemEdit(adapterPosition)
            }
            imgViewDelete.setOnClickListener {
                val builder = AlertDialog.Builder(view.context)
                builder.setIcon(R.drawable.ic_launcher_foreground)
                builder.setTitle(R.string.app_name)
                builder.setMessage("Do you want to delete this user?")
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