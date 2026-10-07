package com.example.timetrekbharat.ui

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.timetrekbharat.adapter.TimelineAdapter
import com.example.timetrekbharat.databinding.ActivityStateDetailBinding
import com.example.timetrekbharat.model.State
import com.example.timetrekbharat.viewmodel.StateDetailViewModel

class StateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStateDetailBinding
    private lateinit var viewModel: StateDetailViewModel
    private lateinit var adapter: TimelineAdapter
    private var currentStateSlug: String? = null
    private var currentStateName: String? = null
    private var currentState: State? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentStateSlug = intent.getStringExtra("EXTRA_STATE_SLUG")
        currentStateName = intent.getStringExtra("EXTRA_STATE_NAME")

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = currentStateName ?: "State History"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewModel = ViewModelProvider(this)[StateDetailViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        if (!currentStateSlug.isNullOrEmpty()) {
            viewModel.setStateSlug(currentStateSlug)
        }
    }

    private fun setupRecyclerView() {
        adapter = TimelineAdapter { entry ->
            val intent = Intent(this@StateDetailActivity, AskHistorianActivity::class.java).apply {
                putExtra("EXTRA_STATE_NAME", currentStateName)
                putExtra("EXTRA_ERA_TITLE", "${entry.era} - ${entry.title}")
                putExtra("EXTRA_DEFAULT_QUESTION", "Tell me about ${entry.title} in $currentStateName during ${entry.period}")
            }
            startActivity(intent)
        }
        binding.rvTimeline.layoutManager = LinearLayoutManager(this)
        binding.rvTimeline.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAskHistorianState.setOnClickListener {
            val intent = Intent(this@StateDetailActivity, AskHistorianActivity::class.java).apply {
                putExtra("EXTRA_STATE_NAME", currentStateName)
            }
            startActivity(intent)
        }

        binding.btnFavoriteDetail.setOnClickListener {
            val state = currentState ?: return@setOnClickListener
            viewModel.toggleFavorite(state)
        }

        binding.btnShareDetail.setOnClickListener {
            val state = currentState ?: return@setOnClickListener
            val shareText = "📜 Discover the rich history of ${state.name} on TimeTrek Bharat!\n\n${state.shortDescription}\n\nDownload TimeTrek Bharat to explore royal timelines."
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "History of ${state.name}")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share History via"))
        }
    }

    private fun observeViewModel() {
        viewModel.stateDetail.observe(this) { state ->
            if (state != null) {
                currentState = state
                bindStateData(state)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.pbTimelineLoading.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this@StateDetailActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindStateData(state: State) {
        currentStateName = state.name
        binding.collapsingToolbar.title = state.name
        binding.tvStateTitleDetail.text = state.name
        binding.tvCapitalDetail.text = "🏛️ Capital: ${state.capital ?: "N/A"}"
        binding.tvRegionDetail.text = "Region: ${state.region ?: "N/A"}"
        binding.tvDescriptionDetail.text = state.shortDescription
        binding.fabAskHistorianState.text = "🤖 Ask AI about ${state.name}"

        if (state.isFavorite) {
            binding.btnFavoriteDetail.setImageResource(com.example.timetrekbharat.R.drawable.ic_star_filled)
        } else {
            binding.btnFavoriteDetail.setImageResource(com.example.timetrekbharat.R.drawable.ic_star_outline)
        }

        val bannerUrl = if (!state.bannerUrl.isNullOrBlank()) state.bannerUrl else state.imageUrl

        if (!bannerUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(bannerUrl)
                .centerCrop()
                .into(binding.ivStateBanner)
        }

        if (state.timeline != null) {
            adapter.setTimelineEntries(state.timeline)
        }
    }
}
