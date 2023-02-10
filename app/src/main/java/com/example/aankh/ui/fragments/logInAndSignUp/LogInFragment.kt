package com.example.aankh.ui.fragments.logInAndSignUp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aankh.DashBoard
import com.example.aankh.dataModels.UserModel
import com.example.aankh.databinding.FragmentLogInBinding
import com.example.aankh.viewModels.authViewModel.LogInUserViewModel


class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LogInUserViewModel


    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(this)[LogInUserViewModel::class.java]


        val preferences = this.requireActivity()
            .getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        val editor = preferences.edit()



        binding.logInButtonFragment.setOnClickListener {
            val id = binding.idNumberINputLogInFragment.text.toString()
            val password = binding.passwordInputLogInFragment.text.toString()
            if (id.isEmpty()) {
                Toast.makeText(requireContext(), "Enter the id number", Toast.LENGTH_LONG).show()
            } else if (password.isEmpty()) {
                Toast.makeText(requireContext(), "Enter the password", Toast.LENGTH_LONG).show()
            } else {
                binding.logInButtonFragment.visibility = View.INVISIBLE
                binding.progressBarLogInFragment.visibility = View.VISIBLE
                loginViewModel.getLogInUserLiveDataViewModel(UserModel(id, password))
                    .observe(this, Observer {
                        val result = it as Boolean
                        if (result) {
                            editor.putString("firstTime", "no").apply()
                            editor.putString("id", id).apply()

                            startActivity(Intent(requireContext(), DashBoard::class.java))
                            requireActivity().finish()
                        } else {

                            Toast.makeText(requireContext(), "Wrong password", Toast.LENGTH_LONG)
                                .show()
                        }
                        binding.logInButtonFragment.visibility = View.VISIBLE
                        binding.progressBarLogInFragment.visibility = View.INVISIBLE
                    })
            }
        }



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}