package com.example.wms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.wms.api.ProductRepository
import com.example.wms.databinding.ActivityNewProductBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewProductActivity : DrawerActivity() {
    private lateinit var imgView: ImageView
    private lateinit var editTxtProdName: EditText
    private lateinit var editTxtDescription: EditText
    private lateinit var editTxtPrice: EditText
    private lateinit var editTxtCategory: EditText
    private lateinit var editTxtQuantity: EditText
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnAddProd: Button
    private lateinit var imgPath: String
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var imgName: String
    private lateinit var imgUri: Uri
    private lateinit var repository: ProductRepository

    private val cameraPermissionCode: Int = 1
    private val cameraRequestCode: Int = 2
    private val galleryRequestCode: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newProductBinding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(newProductBinding.getRoot())
        allocateActivityTitle("New Product")

        storage = FirebaseStorage.getInstance()
        storageReference = storage.getReference()

        // Initialize product repository with context
        repository = ProductRepository(this)

        imgView = newProductBinding.imgViewProductImg
        editTxtProdName = newProductBinding.editTxtProductName
        editTxtDescription = newProductBinding.editTxtDescription
        editTxtPrice = newProductBinding.editTxtPrice
        editTxtCategory = newProductBinding.editTxtCategory
        editTxtQuantity = newProductBinding.editTxtQuantity
        btnCamera = newProductBinding.btnNewImg
        btnGallery = newProductBinding.btnGalleryImg
        btnAddProd = newProductBinding.btnAddNewProduct

        btnCamera.setOnClickListener { askPermission() }
        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, galleryRequestCode)
        }

        btnAddProd.setOnClickListener((View.OnClickListener { view: View? ->
            if (editTxtProdName.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please name your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtDescription.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a description for your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtPrice.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the price of your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtCategory.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the category of your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtQuantity.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the quantity of your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (imgView.drawable == null) {
                Toast.makeText(
                    this,
                    "Please select an image for your product.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadNewProduct(imgName, imgUri)
                Toast.makeText(
                    this,
                    "Your product has been added.",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, ManageProductActivity::class.java))
            }
        }))
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
        permissions: Array<out String>,
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
        val imgFile: File = createImgFile()

        if (true) {
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
                val extension = mime.getExtensionFromMimeType(resolver.getType(imgUri))

                imgName = "JPEG_$time.$extension"
                imgView.setImageURI(imgUri)
            }
        }
    }

    private fun uploadNewProduct(imgName: String, imgUri: Uri) {
        val img = storageReference.child("ProductImg/$imgName")
        img.putFile(imgUri)
        val product = Product(
            null,
            editTxtProdName.text.toString(),
            editTxtDescription.text.toString(),
            editTxtPrice.text.toString().toDouble(),
            editTxtCategory.text.toString(),
            editTxtQuantity.text.toString().toInt(),
            imgName
        )
        repository.createProduct(product, onSuccess = { product ->
            Toast.makeText(
                this,
                "Your product has been added.",
                Toast.LENGTH_SHORT
            ).show()
        }, onError = {
            Toast.makeText(
                this,
                "Failed to add product.",
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}