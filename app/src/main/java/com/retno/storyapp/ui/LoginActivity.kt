package com.retno.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.retno.storyapp.databinding.ActivityLoginBinding
import com.retno.storyapp.helper.SessionManager
import com.retno.storyapp.viemodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        loginViewModel.loginResult.observe(this) { result ->
            binding.progressBarLogin.visibility = View.GONE
            result.onSuccess {
                val loginResult = it.loginResult
                loginResult?.let {
                    lifecycleScope.launch {
                        SessionManager.saveAuthToken(this@LoginActivity, it.token, it.userId)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
            result.onFailure {
                Toast.makeText(this, "Login failed: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (validateInputs(email, password)) {
                binding.progressBarLogin.visibility = View.VISIBLE
                lifecycleScope.launch {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.edLoginEmail.error = "Email cannot be empty"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.edLoginPassword.error = "Password cannot be empty"
            isValid = false
        }

        return isValid
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_Y, -50f, 50f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val emailField = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(5000)
        val passwordField = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(5000)
        val loginButton = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(5000)

        AnimatorSet().apply {
            playSequentially(emailField, passwordField, loginButton)
            start()
        }
    }
}
