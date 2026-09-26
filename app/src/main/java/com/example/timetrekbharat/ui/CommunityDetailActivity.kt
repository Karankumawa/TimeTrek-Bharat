package com.example.timetrekbharat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ActivityCommunityDetailBinding
import com.example.timetrekbharat.model.CommunityPost
import com.example.timetrekbharat.model.SingleCommunityResponse
import com.example.timetrekbharat.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityDetailBinding
    private var post: CommunityPost? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        post = intent.getSerializableExtra("EXTRA_COMMUNITY_POST") as? CommunityPost

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (post != null) {
            bindPostDetails(post!!)
        } else {
            Toast.makeText(this, "Unable to load post details", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun bindPostDetails(item: CommunityPost) {
        binding.collapsingToolbar.title = item.title
        binding.tvTitleDetail.text = item.title
        binding.tvDescriptionDetail.text = item.description
        binding.tvCategoryBadge.text = item.category.ifBlank { "🏛️ Historical Update" }
        binding.tvLocationBadge.text = "${item.locationName} • ${item.stateSlug.uppercase()}"
        binding.tvAuthorDetail.text = "Submitted by: ${item.authorName.ifBlank { "Anonymous" }}"
        binding.tvDateDetail.text = item.createdAt?.take(10) ?: "Recent"
        binding.btnLike.text = "❤️ Like (${item.likesCount})"

        if (item.imageUrl.isNotBlank()) {
            Glide.with(this)
                .load(item.imageUrl)
                .placeholder(R.drawable.placeholder_heritage)
                .error(R.drawable.placeholder_heritage)
                .centerCrop()
                .into(binding.ivDetailBanner)
        } else {
            binding.ivDetailBanner.setImageResource(R.drawable.placeholder_heritage)
        }

        binding.btnLike.setOnClickListener {
            likePost(item.id)
        }

        binding.btnAskAiStory.setOnClickListener {
            val intent = Intent(this, AskHistorianActivity::class.java).apply {
                putExtra("EXTRA_STATE_NAME", item.stateSlug)
                putExtra("EXTRA_ERA_TITLE", item.title)
                putExtra("EXTRA_DEFAULT_QUESTION", "Tell me more about ${item.title} in ${item.locationName}")
            }
            startActivity(intent)
        }
    }

    private fun likePost(postId: String) {
        if (postId.isBlank()) return
        binding.btnLike.isEnabled = false

        RetrofitClient.api.likeCommunityPost(postId).enqueue(object : Callback<SingleCommunityResponse> {
            override fun onResponse(
                call: Call<SingleCommunityResponse>,
                response: Response<SingleCommunityResponse>
            ) {
                binding.btnLike.isEnabled = true
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val updatedPost = response.body()?.data
                    if (updatedPost != null) {
                        post = updatedPost
                        binding.btnLike.text = "❤️ Like (${updatedPost.likesCount})"
                        Toast.makeText(this@CommunityDetailActivity, "Thanks for upvoting!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SingleCommunityResponse>, t: Throwable) {
                binding.btnLike.isEnabled = true
            }
        })
    }
}
