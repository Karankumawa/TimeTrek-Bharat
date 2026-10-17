package com.example.timetrekbharat.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ActivityTimelineDetailBinding
import com.example.timetrekbharat.model.TimelineEntry

class TimelineDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimelineDetailBinding
    private var stateName: String? = null
    private var timelineEntry: TimelineEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimelineDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stateName = intent.getStringExtra("EXTRA_STATE_NAME")
        @Suppress("DEPRECATION")
        timelineEntry = intent.getSerializableExtra("EXTRA_TIMELINE_ENTRY") as? TimelineEntry

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = timelineEntry?.title ?: "Era Details"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        if (timelineEntry != null) {
            bindTimelineEntry(timelineEntry!!)
        }

        binding.btnAskAiDetail.setOnClickListener {
            val entry = timelineEntry
            val intent = Intent(this@TimelineDetailActivity, AskHistorianActivity::class.java).apply {
                putExtra("EXTRA_STATE_NAME", stateName ?: "India")
                putExtra("EXTRA_ERA_TITLE", "${entry?.era ?: ""} - ${entry?.title ?: ""}")
                putExtra("EXTRA_DEFAULT_QUESTION", "Tell me in detail about ${entry?.title ?: ""} during ${entry?.period ?: ""} in ${stateName ?: "India"}")
            }
            startActivity(intent)
        }
    }

    private fun bindTimelineEntry(entry: TimelineEntry) {
        binding.collapsingToolbar.title = entry.title
        binding.tvEraTitleDetail.text = entry.title
        binding.tvEraBadgeDetail.text = entry.era ?: "Historical Era"
        binding.tvPeriodDetail.text = entry.period ?: ""
        binding.tvStateContextDetail.text = "Region: ${stateName ?: "Pan-India Heritage"}"
        binding.tvEraDescriptionDetail.text = entry.description ?: "Historical details available."

        // Era badge color styling
        val era = entry.era?.lowercase() ?: ""
        when {
            era.contains("ancient") -> {
                binding.tvEraBadgeDetail.setBackgroundColor(ContextCompat.getColor(this, R.color.era_ancient_bg))
                binding.tvEraBadgeDetail.setTextColor(ContextCompat.getColor(this, R.color.era_ancient_text))
            }
            era.contains("medieval") -> {
                binding.tvEraBadgeDetail.setBackgroundColor(ContextCompat.getColor(this, R.color.era_medieval_bg))
                binding.tvEraBadgeDetail.setTextColor(ContextCompat.getColor(this, R.color.era_medieval_text))
            }
            era.contains("colonial") -> {
                binding.tvEraBadgeDetail.setBackgroundColor(ContextCompat.getColor(this, R.color.era_colonial_bg))
                binding.tvEraBadgeDetail.setTextColor(ContextCompat.getColor(this, R.color.era_colonial_text))
            }
            else -> {
                binding.tvEraBadgeDetail.setBackgroundColor(ContextCompat.getColor(this, R.color.era_modern_bg))
                binding.tvEraBadgeDetail.setTextColor(ContextCompat.getColor(this, R.color.era_modern_text))
            }
        }

        // Key Events Section
        if (!entry.keyEvents.isNullOrEmpty()) {
            binding.cardKeyEvents.visibility = View.VISIBLE
            val eventsText = StringBuilder()
            for (event in entry.keyEvents!!) {
                eventsText.append("• ").append(event).append("\n")
            }
            binding.tvKeyEventsDetail.text = eventsText.toString().trim()
        } else {
            binding.cardKeyEvents.visibility = View.GONE
        }

        // Key Rulers Section
        if (!entry.keyRulers.isNullOrEmpty()) {
            binding.cardKeyRulers.visibility = View.VISIBLE
            binding.tvKeyRulersDetail.text = entry.keyRulers!!.joinToString(", ")
        } else {
            binding.cardKeyRulers.visibility = View.GONE
        }

        // Banner Image
        if (!entry.imageUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(entry.imageUrl)
                .placeholder(R.drawable.placeholder_heritage)
                .error(R.drawable.placeholder_heritage)
                .centerCrop()
                .into(binding.ivEraBanner)
        } else {
            binding.ivEraBanner.setImageResource(R.drawable.placeholder_heritage)
        }
    }
}
