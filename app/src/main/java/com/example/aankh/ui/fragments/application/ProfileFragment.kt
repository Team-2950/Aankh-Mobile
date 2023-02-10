package com.example.aankh.ui.fragments.application

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.aankh.dataModels.UserProfileData
import com.example.aankh.databinding.FragmentProfileBinding
import com.example.aankh.viewModels.uiViewModels.ProfileViewModel


class ProfileFragment() : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        if (viewModel.isFetched()) {
            val it = viewModel.getUserProfile().value
            if (it != null) {
                updateUi(it)
            }

        } else {
            viewModel.fetchUserProfile()
            viewModel.getUserProfile().observe(viewLifecycleOwner, Observer {
                updateUi(it)
            })

        }

        val preferences =
            activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)
        val id = preferences?.getString("id", "")
        Log.d("user id", id.toString())
        return binding.root
    }

    private fun updateUi(it: UserProfileData) {
        binding.userName.text = it.name
        binding.userId.text = it.id
        binding.userDesignation.text = it.designation
        binding.userDob.text = it.dateOfBirth
        binding.userBloodGroup.text = it.bloodGroup
        binding.userAreaName.text = it.areaName
    }

}