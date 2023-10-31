package com.dicoding.stroyapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.stroyapp.databinding.ActivityDetailBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.viewmodel.DetailViewModel

class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)

        detailViewModel.getToken().observe(this) {
            val intent = Intent(intent)
            val dataID = intent.getStringExtra(EXTRA_ID)

            detailViewModel.getDetailStory(it, dataID.toString())
            setupDetail()
        }
        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupDetail() {
        detailViewModel.detailStory.observe(this) {
            if (it != null) {
                Glide.with(this)
                    .load(it.photoUrl)
                    .into(binding.ivDetailPhoto)
                binding.tvDetailName.text = it.name
                binding.tvDetailDescription.text = it.description
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbDetail.visibility = View.VISIBLE
        } else {
            binding.pbDetail.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}