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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.aankh.R
import com.example.aankh.databinding.FragmentMainDashboardBinding
import com.example.aankh.viewModels.uiViewModels.ProfileViewModel


class MainDashboard : Fragment() {

    private var _binding: FragmentMainDashboardBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentMainDashboardBinding.inflate(inflater, container, false)


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
                profileViewModel.fetchUserProfile(id)
            }
        }


        profileViewModel.getUserProfileData().observe(viewLifecycleOwner, Observer {
            binding.userNameDashBoard.text = it.name
        })





        binding.startTrackingButtonMainDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboard_to_trackingFragment)
        }
        binding.reportFirButtonMainDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboard_to_firFragment)
        }
        binding.profileButtonMainDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_mainDashboard_to_profileFragment)
        }


        return binding.root

    }
}