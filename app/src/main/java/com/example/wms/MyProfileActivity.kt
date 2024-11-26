package com.example.wms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.wms.api.UserRepository
import com.example.wms.databinding.ActivityMyProfileBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.lang.StringBuilder
import java.util.Timer
import java.util.TimerTask

class MyProfileActivity : DrawerActivity() {
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPhone: TextView
    private lateinit var txtViewAddress: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var imgView: ImageView
    private lateinit var imgName: String
    private lateinit var password: String

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myProfileBinding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(myProfileBinding.getRoot())
        allocateActivityTitle("My Profile")

        txtViewUsername = myProfileBinding.txtViewUsername
        txtViewPhone = myProfileBinding.txtViewPhone
        txtViewAddress = myProfileBinding.txtViewAddress
        btnEditProfile = myProfileBinding.btnEditProfile
        imgView = myProfileBinding.imgViewPfp

        // Initialize repository with context
        repository = UserRepository(this)

        setProfile()

        btnEditProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("USERNAME", txtViewUsername.text.toString())
            bundle.putString("PASSWORD", password)
            if (txtViewPhone.text.toString().length == 10)
                bundle.putString("PHONE", txtViewPhone.text.toString())

            val address = txtViewAddress.text.toString()
            try {
                if (!address.isEmpty()) {
                    val city: String? =
                        address.split(", ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[1]
                    val province: String? =
                        address.split(", ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[2]
                    val numberAndStreet =
                        address.split(", ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0]
                    val homeNumber: String? =
                        address.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0]

                    val streetNames =
                        numberAndStreet.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val streetName = StringBuilder()
                    for (i in 1 until streetNames.size) {
                        streetName.append(streetNames[i]).append(" ")
                    }
                    bundle.putString("HOME_NUMBER", homeNumber)
                    bundle.putString("STREET", streetName.toString())
                    bundle.putString("CITY", city)
                    bundle.putString("PROVINCE", province)
                    bundle.putString("IMG_NAME", imgName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val intent: Intent = Intent(this, MyProfileEditActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    fun setProfile() {
        repository.getUser(StoredDataHelper.get(this), onSuccess = { user ->
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
                img.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri?> {
                    override fun onSuccess(uri: Uri?) {
                        Picasso.get().load(uri).into(imgView)
                    }
                })
            }
        }

        val timer = Timer()
        timer.schedule(timerTask, 1500)
    }
}