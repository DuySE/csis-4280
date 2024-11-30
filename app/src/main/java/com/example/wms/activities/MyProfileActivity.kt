package com.example.wms.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.wms.apis.UserRepository
import com.example.wms.databinding.ActivityMyProfileBinding
import com.example.wms.utils.StoredDataHelper
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.Timer
import java.util.TimerTask

class MyProfileActivity : DrawerActivity() {
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPhone: TextView
    private lateinit var txtViewAddress: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var imgView: ImageView
    private lateinit var imgName: String
    private var password: String = ""

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(myProfileBinding.root)

        txtViewUsername = myProfileBinding.txtViewUsername
        txtViewPhone = myProfileBinding.txtViewPhone
        txtViewAddress = myProfileBinding.txtViewAddress
        btnEditProfile = myProfileBinding.btnEditProfile
        imgView = myProfileBinding.imgViewPfp

        // Initialize repository with context
        repository = UserRepository(this)

        // Initialize imgName
        imgName = "default_profile_photo.png"

        setProfile()

        btnEditProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("USERNAME", txtViewUsername.text.toString())
            bundle.putString("PASSWORD", password)
            if (txtViewPhone.text.toString().length == 10)
                bundle.putString("PHONE", txtViewPhone.text.toString())

            val address = txtViewAddress.text.toString()
            if (address.split(", ").size == 3) {
                val city = address.split(", ")[1]
                val province = address.split(", ")[2]
                val numberAndStreet = address.split(", ")[0]
                val homeNumber = numberAndStreet.split(" ")[0]
                val streetName = numberAndStreet.split(" ").drop(1).joinToString(" ")

                bundle.putString("HOME_NUMBER", homeNumber)
                bundle.putString("STREET", streetName.toString())
                bundle.putString("CITY", city)
                bundle.putString("PROVINCE", province)
            }

            bundle.putString("IMG_NAME", imgName)
            val intent: Intent = Intent(this, MyProfileEditActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private fun setProfile() {
        val bundle = intent.extras
        val username: String =
            if (bundle == null) StoredDataHelper.get(this) else bundle.getString("USERNAME")
                .toString()
        repository.getUser(username, onSuccess = { user ->
            allocateActivityTitle(user.username.toString())
            txtViewUsername.text = user.username
            password = user.password
            txtViewAddress.text = user.address
            txtViewPhone.text = user.phone
            imgName = user.profileImg.toString()
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val storage = FirebaseStorage.getInstance()
                val storageReference = storage.getReference()
                val img = storageReference.child("ProfileImg/$imgName")
                img.downloadUrl.addOnSuccessListener { uri ->
                    Picasso.get().load(uri).into(imgView)
                }
            }
        }

        val timer = Timer()
        timer.schedule(timerTask, 1500)
    }
}