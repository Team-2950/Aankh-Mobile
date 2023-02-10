package com.example.aankh.ui.fragments.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.aankh.MainActivity
import com.example.aankh.R
import com.example.aankh.databinding.FragmentLogOutBinding
import com.example.aankh.databinding.FragmentMainDashboardBinding


class LogOutFragment : Fragment() {

    private var _binding: FragmentLogOutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogOutBinding.inflate(inflater, container, false)


        val preferences = this.requireActivity()
            .getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        val editor = preferences.edit()

        binding.yesLogOut.setOnClickListener {
            editor.putString("firstTime", "yes").apply()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }


        binding.noLogOut.setOnClickListener {
            findNavController().popBackStack()
        }


        return binding.root
    }


}