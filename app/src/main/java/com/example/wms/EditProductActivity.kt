package com.example.wms

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
import com.example.wms.api.ProductRepository
import com.example.wms.api.UserRepository
import com.example.wms.databinding.ActivityEditProductBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProductActivity : DrawerActivity() {
    // Code
    private val cameraPermissionCode: Int = 1
    private val cameraRequestCode: Int = 2
    private val galleryRequestCode: Int = 3

    private lateinit var imgView: ImageView
    private lateinit var editTxtProdName: EditText
    private lateinit var editTxtProdDescription: EditText
    private lateinit var editTxtPrice: EditText
    private lateinit var editTxtProdCategory: EditText
    private lateinit var editTxtQuantity: EditText
    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnEditProd: Button
    private lateinit var imgPath: String
    private lateinit var imgName: String
    private var imgUri: Uri? = null
    private var isImageChanged: Boolean = false

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = storage.getReference()

    private lateinit var userRepository: UserRepository
    private lateinit var productRepository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editProductBinding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(editProductBinding.getRoot())
        allocateActivityTitle("Edit Your Product")

        imgView = editProductBinding.imgViewEditProduct
        editTxtProdName = editProductBinding.editTxtProductNameEdit
        editTxtProdDescription = editProductBinding.editTxtDescriptionEdit
        editTxtPrice = editProductBinding.editTxtPriceEdit
        editTxtProdCategory = editProductBinding.editTxtCategoryEdit
        editTxtQuantity = editProductBinding.editTxtQuantityEdit
        btnCamera = editProductBinding.btnNewImageEdit
        btnGallery = editProductBinding.btnGalleryImgEdit
        btnEditProd = editProductBinding.btnEditProduct

        val bundle = intent.extras

        // Initialize repositories with context
        userRepository = UserRepository(this)
        productRepository = ProductRepository(this)

        productRepository.getProduct(bundle?.getString("ID").toString(), onSuccess = { product ->
            editTxtProdName.setText(product.name)
            editTxtProdDescription.setText(product.description)
            editTxtPrice.setText(String.format(Locale.US, "%,.2f", product.price))
            editTxtProdCategory.setText(product.category)
            editTxtQuantity.setText(String.format(Locale.US, "%d", product.quantity))

            val img = storageReference.child("ProductImg/" + product.imgName)
            imgName = product.imgName
            img.getDownloadUrl().addOnSuccessListener(object : OnSuccessListener<Uri?> {
                override fun onSuccess(uri: Uri?) {
                    Picasso.get().load(uri).into(imgView)
                    if (uri != null && isImageChanged) imgUri = uri
                }
            })
        }, onError = { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        })

        btnCamera.setOnClickListener { askPermission() }
        btnGallery.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, galleryRequestCode)
        }

        btnEditProd.setOnClickListener {
            if (editTxtProdName.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please name your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtProdDescription.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter a description for your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtPrice.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the price of your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtProdCategory.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the category of your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTxtQuantity.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter the quantity of your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (imgView.drawable == null) {
                Toast.makeText(
                    this,
                    "Please select an image for your product",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadEditedProduct(imgName, imgUri)

                val updatedProduct = Product(
                    null,
                    editTxtProdName.text.toString(),
                    editTxtProdDescription.text.toString(),
                    editTxtPrice.text.toString().toDouble(),
                    editTxtProdCategory.text.toString(),
                    editTxtQuantity.text.toString().toInt(),
                    imgName
                )
                // Update product in database
                productRepository.updateProduct(
                    bundle?.getString("ID").toString(),
                    updatedProduct,
                    onSuccess = { product ->
                        Toast.makeText(this, "Product updated.", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    })
                // TODO: Handle financial report here
            }
            startActivity(Intent(this, ManageProductActivity::class.java))
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
                    "Camera permission is required to use this feature",
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
                isImageChanged = true
            } else {
                Toast.makeText(this, "Creating file failed", Toast.LENGTH_SHORT).show()
            }
        }

        if (requestCode == galleryRequestCode) {
            if (resultCode == RESULT_OK) {
                imgUri = data?.data
                val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

                val resolver = contentResolver
                val mime = MimeTypeMap.getSingleton()
                val extension = mime.getExtensionFromMimeType(resolver.getType(imgUri!!))

                imgName = "JPEG_$time.$extension"
                imgView.setImageURI(imgUri)
                isImageChanged = true
            }
        }
    }

    private fun uploadEditedProduct(imgName: String?, imgUri: Uri?) {
        val img = storageReference.child("ProductImg/$imgName")
        if (imgUri != null) img.putFile(imgUri)
    }
}