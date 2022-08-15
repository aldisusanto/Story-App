package com.android.submission.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.submission.core.source.remote.response.item.StoryItem
import com.android.submission.databinding.StoryItemBinding
import com.bumptech.glide.Glide
import android.content.Context


class StoryAdapter(private val context: Context) :
    PagingDataAdapter<StoryItem, StoryAdapter.ViewHolder>(mDiffCallback) {
    private var onItemClickCallback: OnItemClickCallback? = null
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    inner class ViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storyItem: StoryItem) {


            binding.root.setOnClickListener {
                onItemClickCallback?.onItemClicked(storyItem)
            }
            with(binding) {
                tvName.text = storyItem.name
                tvDesc.text = storyItem.description
                Glide.with(context)
                    .load(storyItem.photoUrl)
                    .into(ivPost)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyItem: StoryItem)
    }

    companion object {
        val mDiffCallback = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: StoryItem,
                newItem: StoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}