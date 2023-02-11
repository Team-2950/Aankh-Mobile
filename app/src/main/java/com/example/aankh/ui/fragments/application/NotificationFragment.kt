package com.example.aankh.ui.fragments.application

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.aankh.R
import com.example.aankh.databinding.FragmentNotificationBinding
import com.example.aankh.databinding.FragmentTrackingBinding
import com.example.aankh.utils.Constants
import com.example.aankh.utils.Permissions
import com.example.aankh.utils.Permissions.hasCameraPermission
import com.example.aankh.utils.Permissions.requestCameraPermission
import com.example.aankh.viewModels.uiViewModels.ProfileViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class NotificationFragment : Fragment(), EasyPermissions.PermissionCallbacks {


    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null
    private val profileViewModel: ProfileViewModel by activityViewModels()

    private val verificationViewModel :
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)


        binding.check.visibility = View.INVISIBLE
        binding.close.visibility = View.INVISIBLE

        val baseImage = profileViewModel.getUserProfileData().value?.photo









        binding.camera.setOnClickListener {
//            TODO as for permission
            if (hasCameraPermission(requireContext())) {
//                start intent

            } else
                requestCameraPermission(this)


        }



        return binding.root
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
//TODO start the intent
        openCameraInterface()

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