package com.example.aankh.ui.fragments.application

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.aankh.R
import com.example.aankh.dataModels.VerificationImageDataModel
import com.example.aankh.databinding.FragmentNotificationBinding
import com.example.aankh.databinding.FragmentTrackingBinding
import com.example.aankh.utils.Constants
import com.example.aankh.utils.Permissions
import com.example.aankh.utils.Permissions.hasCameraPermission
import com.example.aankh.utils.Permissions.requestCameraPermission
import com.example.aankh.viewModels.uiViewModels.ProfileViewModel
import com.example.aankh.viewModels.uiViewModels.VerificationViewModel
import com.google.firebase.storage.FirebaseStorage
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class NotificationFragment : Fragment(), EasyPermissions.PermissionCallbacks {


    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!


    private val IMAGE_CAPTURE_CODE = 1001

    private val REQUEST_IMAGE_CAPTURE = 1
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val verificationViewModel: VerificationViewModel by activityViewModels()

    private val storage = FirebaseStorage.getInstance()
    private lateinit var preferences: SharedPreferences
    private lateinit var imageUri: String
    private lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

//        binding.check.visibility = View.INVISIBLE
//        binding.close.visibility = View.INVISIBLE
//        binding.resultText.text=""
//
//        try {
//            preferences =
//                activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)!!
//            id = preferences.getString("id", "").toString()
//        } catch (e: Exception) {
//
//        }
//
//        val baseImage = profileViewModel.getUserProfileData().value?.photo
//
////        binding.camera.setOnClickListener {
//////            TODO as for permission
////            if (hasCameraPermission(requireContext())) {
//////                start intent
////
////            } else
////                requestCameraPermission(this)
////
////
////        }
//
//        binding.cameraToclick.setOnClickListener {
//            openCameraInterface();
//        }
//
//
//        val reference = storage.reference.child("Verification Images").child(id)
//        reference.putFile(Uri.parse(imageUri)).addOnSuccessListener { _ ->
//            reference.downloadUrl.addOnSuccessListener { url ->
//                imageUri = url.toString()
//                binding.resultText.text = "processing..."
//            }
//        }
//
//        verificationViewModel.compareImage(
//            id,
//            VerificationImageDataModel("baseImage.toString()", "imageUri")
//        )
//
//        verificationViewModel.getResult().observe(viewLifecycleOwner, Observer {
//            if (it == "success") {
//                binding.resultText.text = "successfully sent"
//            }
//        })


        return binding.root
    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//TODO start the intent
        openCameraInterface()

    }

    private fun openCameraInterface() {
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the URI of the clicked image
            val imageUri: Uri? = data?.data

            // Show a toast with the URI
//            Toast.makeText(this, "Image URI: $imageUri", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            if (requestCode == Constants.CAMERA_PERMISSION_REQUEST_CODE) {
                requestCameraPermission(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}
