package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemHistoryTopicCardBinding
import com.example.timetrekbharat.model.HistoryTopicItem

class HistoryTopicAdapter(
    private val onTopicClickListener: (HistoryTopicItem) -> Unit
) : RecyclerView.Adapter<HistoryTopicAdapter.TopicViewHolder>() {

    private var topicList: List<HistoryTopicItem> = ArrayList()
    private var lastPosition = -1

    fun setTopics(topics: List<HistoryTopicItem>?) {
        if (topics != null) {
            this.topicList = topics
            this.lastPosition = -1
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = ItemHistoryTopicCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topicList[position])
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_animation_fall_down)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = topicList.size

    inner class TopicViewHolder(private val binding: ItemHistoryTopicCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: HistoryTopicItem) {
            binding.tvTopicTitle.text = topic.title
            binding.tvTopicSubtitle.text = topic.subtitle
            binding.tvEraBadge.text = topic.periodEra
            binding.tvTopicDescription.text = topic.description

            if (!topic.dynastyOrParty.isNullOrBlank()) {
                binding.tvDynastyBadge.visibility = View.VISIBLE
                binding.tvDynastyBadge.text = topic.dynastyOrParty
            } else {
                binding.tvDynastyBadge.visibility = View.GONE
            }

            if (topic.prominentFigures.isNotEmpty()) {
                binding.tvKeyFigures.visibility = View.VISIBLE
                binding.tvKeyFigures.text = "👥 Figures: ${topic.prominentFigures.joinToString(", ")}"
            } else {
                binding.tvKeyFigures.visibility = View.GONE
            }

            val factCount = topic.keyFacts.size
            binding.tvFactCount.text = "📜 $factCount Key Historical Facts"

            if (topic.imageUrl.isNotBlank()) {
                Glide.with(itemView.context)
                    .load(topic.imageUrl)
                    .placeholder(R.drawable.placeholder_heritage)
                    .error(R.drawable.placeholder_heritage)
                    .centerCrop()
                    .into(binding.ivTopicImage)
            } else {
                binding.ivTopicImage.setImageResource(R.drawable.placeholder_heritage)
            }

            itemView.setOnClickListener {
                itemView.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(120)
                    .withEndAction {
                        itemView.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(120)
                            .withEndAction {
                                onTopicClickListener(topic)
                            }
                            .start()
                    }
                    .start()
            }
        }
    }
}
