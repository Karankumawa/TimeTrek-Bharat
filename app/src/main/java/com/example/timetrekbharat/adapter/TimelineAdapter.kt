package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemTimelineEntryBinding
import com.example.timetrekbharat.model.TimelineEntry

class TimelineAdapter(
    private val onAskAiClickListener: (TimelineEntry) -> Unit
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private var timelineEntries: List<TimelineEntry> = ArrayList()
    private var lastPosition = -1

    fun setTimelineEntries(entries: List<TimelineEntry>?) {
        if (entries != null) {
            this.timelineEntries = entries
            this.lastPosition = -1
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineEntryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(timelineEntries[position], position == timelineEntries.size - 1)
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_animation_slide_right)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = timelineEntries.size

    inner class TimelineViewHolder(private val binding: ItemTimelineEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: TimelineEntry, isLastItem: Boolean) {
            val context = itemView.context

            binding.tvEraBadge.text = entry.era
            binding.tvPeriod.text = entry.period
            binding.tvEraTitle.text = entry.title
            binding.tvEraDescription.text = entry.description

            val era = entry.era?.lowercase() ?: ""
            when {
                era.contains("ancient") -> {
                    binding.tvEraBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.era_ancient_bg))
                    binding.tvEraBadge.setTextColor(ContextCompat.getColor(context, R.color.era_ancient_text))
                    binding.vTimelineDot.setBackgroundColor(ContextCompat.getColor(context, R.color.era_ancient_text))
                }
                era.contains("medieval") -> {
                    binding.tvEraBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.era_medieval_bg))
                    binding.tvEraBadge.setTextColor(ContextCompat.getColor(context, R.color.era_medieval_text))
                    binding.vTimelineDot.setBackgroundColor(ContextCompat.getColor(context, R.color.era_medieval_text))
                }
                era.contains("colonial") -> {
                    binding.tvEraBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.era_colonial_bg))
                    binding.tvEraBadge.setTextColor(ContextCompat.getColor(context, R.color.era_colonial_text))
                    binding.vTimelineDot.setBackgroundColor(ContextCompat.getColor(context, R.color.era_colonial_text))
                }
                else -> {
                    binding.tvEraBadge.setBackgroundColor(ContextCompat.getColor(context, R.color.era_modern_bg))
                    binding.tvEraBadge.setTextColor(ContextCompat.getColor(context, R.color.era_modern_text))
                    binding.vTimelineDot.setBackgroundColor(ContextCompat.getColor(context, R.color.era_modern_text))
                }
            }

            // Pulsing dot animation
            binding.vTimelineDot.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(500)
                .withEndAction {
                    binding.vTimelineDot.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(500)
                        .start()
                }
                .start()

            binding.vTimelineLine.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE

            if (!entry.keyEvents.isNullOrEmpty()) {
                binding.tvKeyEventsHeader.visibility = View.VISIBLE
                binding.tvKeyEvents.visibility = View.VISIBLE
                val eventsText = StringBuilder()
                for (event in entry.keyEvents!!) {
                    eventsText.append("• ").append(event).append("\n")
                }
                binding.tvKeyEvents.text = eventsText.toString().trim()
            } else {
                binding.tvKeyEventsHeader.visibility = View.GONE
                binding.tvKeyEvents.visibility = View.GONE
            }

            if (!entry.keyRulers.isNullOrEmpty()) {
                binding.tvKeyRulersHeader.visibility = View.VISIBLE
                binding.tvKeyRulers.visibility = View.VISIBLE
                binding.tvKeyRulers.text = entry.keyRulers!!.joinToString(", ")
            } else {
                binding.tvKeyRulersHeader.visibility = View.GONE
                binding.tvKeyRulers.visibility = View.GONE
            }

            // Main Featured Era Image
            if (!entry.imageUrl.isNullOrBlank()) {
                binding.cardImageFrame.visibility = View.VISIBLE
                Glide.with(context)
                    .load(entry.imageUrl)
                    .placeholder(R.drawable.placeholder_heritage)
                    .error(R.drawable.placeholder_heritage)
                    .centerCrop()
                    .into(binding.ivEraImage)
            } else {
                binding.cardImageFrame.visibility = View.GONE
            }

            // Multi-Image Gallery Setup
            val galleryImages = ArrayList<String>()
            if (!entry.images.isNullOrEmpty()) {
                galleryImages.addAll(entry.images!!)
            } else if (!entry.imageUrl.isNullOrBlank()) {
                galleryImages.add(entry.imageUrl!!)
            }

            if (galleryImages.size > 1) {
                binding.tvGalleryTitle.visibility = View.VISIBLE
                binding.rvEraGallery.visibility = View.VISIBLE
                binding.rvEraGallery.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.HORIZONTAL, false
                )
                binding.rvEraGallery.adapter = ImageGalleryAdapter(galleryImages) { selectedUrl ->
                    binding.cardImageFrame.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(selectedUrl)
                        .placeholder(R.drawable.placeholder_heritage)
                        .error(R.drawable.placeholder_heritage)
                        .centerCrop()
                        .into(binding.ivEraImage)
                }
            } else {
                binding.tvGalleryTitle.visibility = View.GONE
                binding.rvEraGallery.visibility = View.GONE
            }

            binding.btnAskAiEra.setOnClickListener {
                onAskAiClickListener(entry)
            }
        }
    }
}
