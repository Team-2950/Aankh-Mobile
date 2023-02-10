package com.example.aankh.ui.fragments.application

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        Log.d("view model fragment", viewModel.toString())


        val preferences =
            activity?.getSharedPreferences("PREFERENCE", AppCompatActivity.MODE_PRIVATE)
        val id = preferences?.getString("id", "")

        id?.let {
            if (id == "") {
                Toast.makeText(
                    requireContext(),
                    "ID is empty log out and then log in again!!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (viewModel.isFetched()) {
                    updateUi(viewModel.getUserProfileData().value!!, id)
                } else {
                    viewModel.fetchUserProfile(id)
                    viewModel.getUserProfileData().observe(viewLifecycleOwner, Observer { data ->
                        updateUi(data, it)
                    })
                }
            }
        }

        return binding.root
    }

    private fun updateUi(it: UserProfileData, id: String) {

        binding.userName.text = it.name
        binding.userId.text = id
        binding.userDesignation.text = it.designation
        binding.userDob.text = it.dateofbirth
        binding.userBloodGroup.text = it.bloodgroup
        binding.userAreaName.text = it.area
    }

}