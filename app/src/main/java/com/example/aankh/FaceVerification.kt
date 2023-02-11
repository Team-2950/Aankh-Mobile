package com.example.aankh

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.net.URI
import java.util.*

class FaceVerification : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 130105
    private var currentPhotoPath: String = ""
    private var imageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_verification)

        val openCamera = findViewById<CardView>(R.id.openCameraBtn)

        openCamera.setOnClickListener {
            if (!checkPermission(Manifest.permission.CAMERA)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
                }
            }
            else{
                dispatchTakePictureIntent()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.aankh",
                        it
                    )
                    imageUri = photoURI
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, currentPhotoPath , Toast.LENGTH_SHORT).show()
            val imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val bitmap = MediaStore.Images.Media.getBitmap(this?.contentResolver, imageUri)
            val cardView1  = findViewById<CardView>(R.id.imageViewContainer)
            val submitBtn = findViewById<CardView>(R.id.submitButton)
            val imageView = findViewById<ImageView>(R.id.cameraContent)
            findViewById<CardView>(R.id.openCameraBtn).visibility = View.INVISIBLE
            cardView1.visibility = View.VISIBLE
            submitBtn.visibility = View.VISIBLE
//            imageView.setImageBitmap(imageBitmap)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }
}