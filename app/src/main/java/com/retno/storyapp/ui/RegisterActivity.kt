package com.retno.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.retno.storyapp.api.ApiConfig
import com.retno.storyapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.edRegisterEmail.addTextChangedListener { text ->
            if (!text.isNullOrEmpty() && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                binding.edRegisterEmail.error = "Please enter a valid email"
            }
        }

        binding.edRegisterPassword.addTextChangedListener { text ->
            if (!text.isNullOrEmpty() && text.length < 8) {
                binding.edRegisterPassword.error = "Password must be at least 8 characters"
            }
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (validateRegistration(name, email, password)) {
                showLoading(true)

                lifecycleScope.launch {
                    try {
                        val token = "your_token_here"
                        val apiService = ApiConfig.getApiService(token)
                        val response = apiService.register(name, email, password)

                        showLoading(false)

                        if (response.isSuccessful && response.body() != null && !response.body()!!.error!!) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            val errorMessage = response.body()?.message ?: "Registration failed"
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        showLoading(false)
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please check your input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageViewRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val nameField = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(5000)
        val emailField = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(5000)
        val passwordField = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(5000)
        val registerButton = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(5000)
        val infoText = ObjectAnimator.ofFloat(binding.textViewInfoRegister, View.ALPHA, 1f).setDuration(5000)

        AnimatorSet().apply {
            playSequentially(infoText, nameField, emailField, passwordField, registerButton)
            start()
        }
    }

    private fun validateRegistration(name: String, email: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.edRegisterName.error = "Name cannot be empty"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.edRegisterEmail.error = "Email cannot be empty"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edRegisterEmail.error = "Please enter a valid email"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.edRegisterPassword.error = "Password cannot be empty"
            isValid = false
        } else if (password.length < 8) {
            binding.edRegisterPassword.error = "Password must be at least 8 characters"
            isValid = false
        }

        return isValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.edRegisterName.isEnabled = !isLoading
        binding.edRegisterEmail.isEnabled = !isLoading
        binding.edRegisterPassword.isEnabled = !isLoading
    }
}
