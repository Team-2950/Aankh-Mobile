package com.example.aankh

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aankh.databinding.ActivityMainBinding
import com.example.aankh.ui.fragments.logInAndSignUp.LogInFragment
import com.example.aankh.ui.fragments.logInAndSignUp.SignUpFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(LogInFragment())

        binding.logInButtonMainActivity.setOnClickListener {
            binding.logInButtonUnderlineMainActivity.visibility = View.VISIBLE
            binding.signUpButtonUnderlineMainActivity.visibility = View.INVISIBLE
            replaceFragment(LogInFragment())
        }
        binding.signUpButtonMainActivity.setOnClickListener {
            binding.logInButtonUnderlineMainActivity.visibility = View.INVISIBLE
            binding.signUpButtonUnderlineMainActivity.visibility = View.VISIBLE
            replaceFragment(SignUpFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.framelayout, fragment).commit()
    }
}