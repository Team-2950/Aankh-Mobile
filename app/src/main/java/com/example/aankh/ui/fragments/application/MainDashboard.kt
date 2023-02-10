package com.example.aankh.ui.fragments.application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aankh.R
import com.example.aankh.databinding.FragmentMainDashboardBinding


class MainDashboard : Fragment() {

    private var _binding: FragmentMainDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentMainDashboardBinding.inflate(inflater, container, false)

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