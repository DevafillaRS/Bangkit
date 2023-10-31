package com.dicoding.stroyapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.stroyapp.activity.DetailActivity
import com.dicoding.stroyapp.databinding.StoryListBinding
import com.dicoding.stroyapp.response.ListStoryItem

class PrimaryAdapter :
    PagingDataAdapter<ListStoryItem, PrimaryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryListBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
        getItem(position)?.let { holder.bind(it) }
    }

    inner class MyViewHolder(private val binding: StoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(data.photoUrl)
                    .into(imageStory)
                tvUser.text = data.name
            }
            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra(DetailActivity.EXTRA_ID, data.id)
                itemView.context.startActivity(
                    intentDetail,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                        .toBundle()
                )
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}