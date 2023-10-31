package com.dicoding.stroyapp.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.stroyapp.R
import com.dicoding.stroyapp.adapter.PrimaryAdapter
import com.dicoding.stroyapp.databinding.ActivityMainBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityMainBinding

    private lateinit var rvStory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupButtons()

        val storyAdapter = PrimaryAdapter()
        binding.rvStory.adapter = storyAdapter
        mainViewModel.getToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                observeStoryData()
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeStoryData()
    }

    private fun initializeViews() {
        rvStory = binding.rvStory
        rvStory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)

        mainViewModel.isLoading.observe(this) { showLoading(it) }

        mainViewModel.getToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                showLoading(false)
                mainViewModel.story(token)
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupRecyclerView() {
        val storyAdapter = PrimaryAdapter()
        rvStory.adapter = storyAdapter

        mainViewModel.user.observe(this) {
            showLoading(false)
        }
    }

    private fun setupButtons() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }

        binding.fabMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeStoryData() {
        val storyAdapter = PrimaryAdapter()
        binding.rvStory.adapter = storyAdapter

        mainViewModel.getToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                mainViewModel.story(token).observe(this) { paging ->
                    storyAdapter.submitData(lifecycle, paging)
                }
            }
        }
    }
}
