package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemTimelineEntryBinding
import com.example.timetrekbharat.model.TimelineEntry

class TimelineAdapter(
    private val onAskAiClickListener: (TimelineEntry) -> Unit
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    private var timelineEntries: List<TimelineEntry> = ArrayList()

    fun setTimelineEntries(entries: List<TimelineEntry>?) {
        if (entries != null) {
            this.timelineEntries = entries
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

            if (!entry.imageUrl.isNullOrBlank()) {
                binding.ivEraImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(entry.imageUrl)
                    .centerCrop()
                    .into(binding.ivEraImage)
            } else {
                binding.ivEraImage.visibility = View.GONE
            }

            binding.btnAskAiEra.setOnClickListener {
                onAskAiClickListener(entry)
            }
        }
    }
}
