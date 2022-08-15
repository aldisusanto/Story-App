package com.android.submission.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.android.submission.R
import com.android.submission.core.source.remote.response.item.StoryItem
import com.android.submission.databinding.ActivityDetailBinding
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding?.ivBack?.setOnClickListener(this)
        setupDetail()
    }

    private fun setupDetail() {
        val json = intent?.getStringExtra("story")
        val story = Gson().fromJson(json, StoryItem::class.java)
        binding?.apply {
            tvName.text = story.name
            tvDesc.text = story.description
            Picasso.get()
                .load(story.photoUrl)
                .into(ivPost)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_back -> {
                onBackPressed()
            }
        }
    }
}