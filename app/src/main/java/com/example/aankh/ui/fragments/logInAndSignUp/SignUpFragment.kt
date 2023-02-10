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
import com.example.aankh.databinding.FragmentSignUpBinding
import com.example.aankh.viewModels.authViewModels.CreateUserViewModel


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var createUserViewModel: CreateUserViewModel

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        createUserViewModel = ViewModelProvider(this)[CreateUserViewModel::class.java]


        val preferences = this.requireActivity()
            .getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        val editor = preferences.edit()



        binding.signUpbuttonFragment.setOnClickListener {
            val id = binding.idNumberInputSignUpFragment.text.toString()
            val password = binding.passwordInputSignUpFramgent.text.toString()
            val confirmPassword = binding.confirmPasswordInputSignUpFramgent.text.toString()
            if (id.isEmpty()) {
                Toast.makeText(requireContext(), "Enter the user id", Toast.LENGTH_LONG).show()

            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Enter the same password", Toast.LENGTH_LONG)
                    .show()
            } else {
                binding.signUpbuttonFragment.visibility = View.INVISIBLE
                binding.progressBarSignUpFragment.visibility = View.VISIBLE
                createUserViewModel.getCreateUserLiveDataViewModel(UserModel(id, password))
                    .observe(this,
                        Observer {
                            val result = it as Boolean
                            if (result) {
                                editor.putString("firstTime", "no").apply()
                                editor.putString("id", id).apply()

                                startActivity(Intent(requireContext(), DashBoard::class.java))
                                requireActivity().finish()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Internet issue",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            binding.signUpbuttonFragment.visibility = View.VISIBLE
                            binding.progressBarSignUpFragment.visibility = View.INVISIBLE
                        })
            }

        }


        return binding.root
    }

}