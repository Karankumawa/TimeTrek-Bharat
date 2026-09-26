package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemCommunityPostBinding
import com.example.timetrekbharat.model.CommunityPost
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CommunityPostAdapter(
    private val onPostClickListener: (CommunityPost) -> Unit
) : RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder>() {

    private var postList: List<CommunityPost> = ArrayList()

    fun setPosts(posts: List<CommunityPost>?) {
        if (posts != null) {
            this.postList = posts
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemCommunityPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int = postList.size

    inner class PostViewHolder(private val binding: ItemCommunityPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: CommunityPost) {
            binding.tvPostTitle.text = post.title
            binding.tvPostDescription.text = post.description
            binding.tvPostAuthor.text = "By: ${post.authorName.ifBlank { "Anonymous" }}"

            val stateDisplay = post.stateSlug.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            val locationText = if (stateDisplay.isNotBlank()) {
                "${post.locationName} • $stateDisplay"
            } else {
                post.locationName
            }
            binding.tvLocationBadge.text = locationText

            if (!post.createdAt.isNullOrBlank()) {
                binding.tvPostDate.text = formatDate(post.createdAt!!)
            } else {
                binding.tvPostDate.text = "Recent"
            }

            if (post.imageUrl.isNotBlank()) {
                binding.ivPostImage.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .placeholder(R.drawable.placeholder_heritage)
                    .error(R.drawable.placeholder_heritage)
                    .centerCrop()
                    .into(binding.ivPostImage)
            } else {
                binding.ivPostImage.visibility = View.GONE
            }

            itemView.setOnClickListener {
                itemView.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(100)
                    .withEndAction {
                        itemView.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .withEndAction {
                                onPostClickListener(post)
                            }
                            .start()
                    }
                    .start()
            }
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(isoDate)
                val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                formatter.format(date ?: return isoDate)
            } catch (_: Exception) {
                isoDate.take(10)
            }
        }
    }
}
