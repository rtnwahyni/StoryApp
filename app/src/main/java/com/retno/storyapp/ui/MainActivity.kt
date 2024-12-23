package com.retno.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.retno.storyapp.MapsActivity
import com.retno.storyapp.R
import com.retno.storyapp.StoryViewModelFactory
import com.retno.storyapp.adapter.StoryAdapter
import com.retno.storyapp.api.Injection
import com.retno.storyapp.databinding.ActivityMainBinding
import com.retno.storyapp.helper.SessionManager
import com.retno.storyapp.viemodel.StoryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: StoryViewModel by viewModels {
        StoryViewModelFactory(Injection.provideRepository(applicationContext))
    }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            validateSession()
        }

        setupUI()
        observeViewModel()
    }

    private suspend fun validateSession() {
        try {
            val token = SessionManager.getAuthToken(this@MainActivity).first()
            if (token.isNullOrEmpty()) {
                navigateToWelcome()
            } else {
                Toast.makeText(this@MainActivity, "Welcome back!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@MainActivity,
                "Error retrieving session. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()
            navigateToWelcome()
        }
    }

    private fun setupUI() {
        setupRecyclerView()

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemInsets.top,
                bottom = systemInsets.bottom
            )
            insets
        }

        fetchStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { stories ->
            if (stories.isNullOrEmpty()) {
                Toast.makeText(this, "No stories available", Toast.LENGTH_SHORT).show()
            } else {
                storyAdapter.submitList(stories)
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addStorySuccess.observe(this) { success ->
            val message = if (success) "Story added successfully" else "Failed to add story"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchStories() {
        lifecycleScope.launch {
            viewModel.fetchStories()
        }
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_location -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            SessionManager.clearSession(this@MainActivity)
            Toast.makeText(this@MainActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onResume() {
        super.onResume()
        fetchStories()
    }
}
