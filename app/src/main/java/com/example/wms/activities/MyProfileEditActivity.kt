package com.example.wms.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.example.wms.R
import com.example.wms.apis.ProductRepository
import com.example.wms.apis.UserRepository
import com.example.wms.databinding.ActivityMyProfileEditBinding
import com.example.wms.helpers.StoredDataHelper
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyProfileEditActivity : DrawerActivity() {
    private lateinit var editTxtUsername: EditText
    private lateinit var editTxtHomeNumber: EditText
    private lateinit var editTxtStreetName: EditText
    private lateinit var editTxtCity: EditText
    private lateinit var editTxtProvince: EditText
    private lateinit var editTxtPhone: EditText
    private lateinit var imgView: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var editTxtNewPassword: EditText
    private lateinit var editTxtCurrentPassword: EditText
    private lateinit var imgPath: String
    private val cameraPermissionCode: Int = 1
    private val cameraRequestCode: Int = 2
    private val galleryRequestCode: Int = 3
    private var imgUri: Uri? = null
    private lateinit var imgName: String
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var userRepository: UserRepository
    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myProfileEditBinding = ActivityMyProfileEditBinding.inflate(layoutInflater)
        setContentView(myProfileEditBinding.root)
        allocateActivityTitle("Edit Profile")

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        editTxtUsername = myProfileEditBinding.editTxtUsername
        editTxtNewPassword = myProfileEditBinding.editTxtPassword
        editTxtHomeNumber = myProfileEditBinding.editTxtHomeNumber
        editTxtStreetName = myProfileEditBinding.editTxtStreet
        editTxtCity = myProfileEditBinding.editTxtCity
        editTxtProvince = myProfileEditBinding.editTxtProvince
        editTxtPhone = myProfileEditBinding.editTxtPhone
        editTxtCurrentPassword = myProfileEditBinding.editTxtCurrentPassword
        val btnSave = myProfileEditBinding.btnSaveChanges
        btnCamera = myProfileEditBinding.btnCamera
        btnGallery = myProfileEditBinding.btnGallery
        imgView = myProfileEditBinding.imgViewEditPfp

        // Initialize repositories with context
        userRepository = UserRepository(this)
        productRepository = ProductRepository(this)

        val inBundle = intent.extras
        editTxtUsername.setText(inBundle?.getString("USERNAME", "New Username"))
        editTxtPhone.setText(inBundle?.getString("PHONE"))
        editTxtHomeNumber.setText(inBundle?.getString("HOME_NUMBER"))
        editTxtStreetName.setText(inBundle?.getString("STREET"))
        editTxtCity.setText(inBundle?.getString("CITY"))
        editTxtProvince.setText(inBundle?.getString("PROVINCE"))
        imgName = inBundle?.getString("IMG_NAME").toString()

        imgView.setImageResource(R.drawable.default_profile_photo)

        // Admin does not need to enter current password of user
        myProfileEditBinding.txtFieldCurrentPassword.isVisible =
            StoredDataHelper.get(this) == editTxtUsername.text.toString()

        val img: StorageReference = storageReference.child("ProfileImg/$imgName")
        img.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imgView)
            imgUri = uri
        }

        btnCamera.setOnClickListener { askPermission() }

        editTxtUsername.isEnabled = false

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, galleryRequestCode)
        }

        btnSave.setOnClickListener {
            userRepository.getUser(editTxtUsername.text.toString(), onSuccess = { user ->
                if (editTxtUsername.text.toString().isEmpty() ||
                    (editTxtCurrentPassword.text.toString()
                        .isEmpty() && StoredDataHelper.get(this) == user.username) ||
                    editTxtHomeNumber.text.toString().isEmpty() ||
                    editTxtStreetName.text.toString().isEmpty() ||
                    editTxtCity.text.toString().isEmpty() ||
                    editTxtProvince.text.toString().isEmpty() ||
                    editTxtPhone.text.toString().isEmpty()
                )
                    Toast.makeText(
                        this,
                        "Please enter all the required fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                else if (editTxtPhone.text.toString().length != 10)
                    Toast.makeText(
                        this,
                        "Please enter a 10-digit phone number.", Toast.LENGTH_SHORT
                    ).show()
                else {
                    uploadEditedProfile(imgName, imgUri)
                    userRepository.getUser(user.username, onSuccess = { data ->
                        if (editTxtCurrentPassword.text.toString().isNotEmpty() && !BCrypt.checkpw(
                                editTxtCurrentPassword.text.toString(),
                                data.password
                            ) && StoredDataHelper.get(this) == editTxtUsername.text.toString()
                        ) {
                            Toast.makeText(
                                this,
                                "Current password is incorrect.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            user.username = editTxtUsername.text.toString()
                            if (!editTxtNewPassword.text.toString().isEmpty()) user.password =
                                BCrypt.hashpw(editTxtNewPassword.text.toString(), BCrypt.gensalt())
                            else user.password = editTxtCurrentPassword.text.toString()
                            val address =
                                "${editTxtHomeNumber.text} ${editTxtStreetName.text}, " +
                                        "${editTxtCity.text}, ${editTxtProvince.text}"
                            user.address = address
                            user.phone = editTxtPhone.text.toString()
                            user.profileImg = imgName

                            // Update a user
                            userRepository.updateUser(
                                user.username,
                                user,
                                onSuccess = { _ ->
                                    if (StoredDataHelper.get(this) == editTxtUsername.text.toString())
                                        StoredDataHelper.save(
                                            this@MyProfileEditActivity,
                                            editTxtUsername.text.toString()
                                        )
                                },
                                onError = { error ->
                                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                                })
                            val bundle: Bundle = Bundle()
                            val profileIntent = Intent(this, MyProfileActivity::class.java)
                            bundle.putString("USERNAME", editTxtUsername.text.toString())
                            profileIntent.putExtras(bundle)
                            startActivity(profileIntent)
                        }
                    }, onError = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    })
                }
            }, onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        } else {
            takePicture()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to use this feature.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var imgFile: File? = null
        try {
            imgFile = createImgFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        if (imgFile != null) {
            val imgURI =
                FileProvider.getUriForFile(this, "com.example.android.fileprovider", imgFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgURI)
            startActivityForResult(intent, cameraRequestCode)
        }
    }

    @Throws(IOException::class)
    private fun createImgFile(): File {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imgFileName = "JPEG_" + time + "_"
        val storageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        val imgFile = File.createTempFile(imgFileName, ".jpg", storageDirectory)
        imgPath = imgFile.absolutePath
        return imgFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestCode) {
            if (resultCode == RESULT_OK) {
                val imgFile = File(imgPath)
                imgUri = Uri.fromFile(imgFile)
                imgView.setImageURI(imgUri)
                imgName = imgFile.name

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = imgUri
                this.sendBroadcast(mediaScanIntent)
            } else {
                Toast.makeText(this, "Creating file failed.", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == galleryRequestCode) {
            if (resultCode == RESULT_OK) {
                imgUri = data?.data!!
                val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

                val resolver = contentResolver
                val mime = MimeTypeMap.getSingleton()
                val extension = mime.getExtensionFromMimeType(resolver.getType(imgUri!!))

                imgName = "JPEG_$time.$extension"
                imgView.setImageURI(imgUri)
            }
        }
    }

    private fun uploadEditedProfile(imgName: String, imgUri: Uri?) {
        val img = storageReference.child("ProfileImg/$imgName")
        if (imgUri != null) img.putFile(imgUri)
    }
}