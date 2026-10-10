package com.example.timetrekbharat.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.adapter.HistoryTopicAdapter
import com.example.timetrekbharat.adapter.StateAdapter
import com.example.timetrekbharat.databinding.ActivityMainBinding
import com.example.timetrekbharat.databinding.DialogExplorerHubBinding
import com.example.timetrekbharat.databinding.DialogTopicDetailBinding
import com.example.timetrekbharat.model.HistoryCategory
import com.example.timetrekbharat.model.HistoryTopicItem
import com.example.timetrekbharat.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var stateAdapter: StateAdapter
    private lateinit var topicAdapter: HistoryTopicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupAdapters()
        setupCategoryAndFilterListeners()
        setupHeaderQuickAccess()
        observeViewModel()
    }

    private fun setupAdapters() {
        stateAdapter = StateAdapter(
            onStateClickListener = { state ->
                val intent = Intent(this@MainActivity, StateDetailActivity::class.java).apply {
                    putExtra("EXTRA_STATE_SLUG", state.slug)
                    putExtra("EXTRA_STATE_NAME", state.name)
                }
                startActivity(intent)
            },
            onFavoriteClickListener = { state ->
                viewModel.toggleFavorite(state)
            }
        )

        topicAdapter = HistoryTopicAdapter { topic ->
            showTopicDetailBottomSheet(topic)
        }

        binding.rvStates.layoutManager = LinearLayoutManager(this)
        binding.rvStates.adapter = stateAdapter
    }

    private fun setupHeaderQuickAccess() {
        binding.cardQuickSmritiWalk.setOnClickListener {
            startActivity(Intent(this@MainActivity, SmritiWalkActivity::class.java))
        }

        binding.cardQuickAskHistorian.setOnClickListener {
            startActivity(Intent(this@MainActivity, AskHistorianActivity::class.java))
        }

        binding.cardQuickCommunity.setOnClickListener {
            startActivity(Intent(this@MainActivity, CommunityActivity::class.java))
        }
    }

    private fun setupCategoryAndFilterListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshStates()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.refreshStates()
        }

        binding.etSearchState.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                viewModel.setSearchQuery(query)
                binding.btnClearSearch.visibility = if (query.isNotBlank()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etSearchState.setText("")
        }

        // Apply initial chip styling
        updateCategoryChipStyles(binding.chipGroupHistoryCategories.checkedChipId)
        updateRegionChipStyles(binding.chipGroupRegions.checkedChipId)

        // History Category Selector
        binding.chipGroupHistoryCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val selectedId = checkedIds[0]
            updateCategoryChipStyles(selectedId)

            val category = when (selectedId) {
                R.id.chipCategoryKings -> HistoryCategory.KINGS
                R.id.chipCategoryPolitics -> HistoryCategory.POLITICS
                R.id.chipCategoryAncient -> HistoryCategory.ANCIENT
                R.id.chipCategoryModern -> HistoryCategory.MODERN
                else -> HistoryCategory.STATES
            }

            viewModel.setCategory(category)
            updateUiForCategory(category)
        }

        // Region Filter for States
        binding.chipGroupRegions.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val selectedId = checkedIds[0]
            updateRegionChipStyles(selectedId)

            val filter = when (selectedId) {
                R.id.chipFavorites -> "Favorites"
                R.id.chipNorth -> "North"
                R.id.chipSouth -> "South"
                R.id.chipWest -> "West"
                R.id.chipEast -> "East"
                R.id.chipCentral -> "Central"
                R.id.chipNorthEast -> "North-East"
                else -> "All"
            }
            viewModel.setRegionFilter(filter)
        }

        binding.fabExplorerHub.setOnClickListener {
            showExplorerHubBottomSheet()
        }
    }

    private fun updateCategoryChipStyles(selectedId: Int) {
        val categoryChips = listOf(
            binding.chipCategoryStates,
            binding.chipCategoryKings,
            binding.chipCategoryPolitics,
            binding.chipCategoryAncient,
            binding.chipCategoryModern
        )

        categoryChips.forEach { chip ->
            if (chip.id == selectedId) {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold_royal))
                chip.setTextColor(ContextCompat.getColor(this, R.color.black))
                chip.elevation = 6f
                chip.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start()
            } else {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_bg_unselected))
                chip.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                chip.elevation = 0f
                chip.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
            }
        }
    }

    private fun updateRegionChipStyles(selectedId: Int) {
        val regionChips = listOf(
            binding.chipAll,
            binding.chipFavorites,
            binding.chipNorth,
            binding.chipSouth,
            binding.chipWest,
            binding.chipEast,
            binding.chipCentral,
            binding.chipNorthEast
        )

        regionChips.forEach { chip ->
            if (chip.id == selectedId) {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gold_royal))
                chip.setTextColor(ContextCompat.getColor(this, R.color.black))
                chip.elevation = 6f
                chip.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).start()
            } else {
                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chip_bg_unselected))
                chip.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                chip.elevation = 0f
                chip.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
            }
        }
    }

    private fun updateUiForCategory(category: HistoryCategory) {
        when (category) {
            HistoryCategory.STATES -> {
                binding.svRegionFilters.visibility = View.VISIBLE
                binding.etSearchState.hint = "Search states, capitals, regions..."
                binding.rvStates.adapter = stateAdapter
            }
            HistoryCategory.KINGS -> {
                binding.svRegionFilters.visibility = View.GONE
                binding.etSearchState.hint = "Search kings, emperors, dynasties..."
                binding.rvStates.adapter = topicAdapter
            }
            HistoryCategory.POLITICS -> {
                binding.svRegionFilters.visibility = View.GONE
                binding.etSearchState.hint = "Search freedom movement, leaders, policy..."
                binding.rvStates.adapter = topicAdapter
            }
            HistoryCategory.ANCIENT -> {
                binding.svRegionFilters.visibility = View.GONE
                binding.etSearchState.hint = "Search ancient eras, civilizations..."
                binding.rvStates.adapter = topicAdapter
            }
            HistoryCategory.MODERN -> {
                binding.svRegionFilters.visibility = View.GONE
                binding.etSearchState.hint = "Search modern history, space, reforms..."
                binding.rvStates.adapter = topicAdapter
            }
        }
    }

    private fun showTopicDetailBottomSheet(topic: HistoryTopicItem) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val detailBinding = DialogTopicDetailBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(detailBinding.root)

        detailBinding.tvDetailTitle.text = topic.title
        detailBinding.tvDetailSubtitle.text = topic.subtitle
        detailBinding.tvDetailEra.text = topic.periodEra
        detailBinding.tvDetailDescription.text = topic.description

        if (!topic.dynastyOrParty.isNullOrBlank()) {
            detailBinding.tvDetailDynasty.visibility = View.VISIBLE
            detailBinding.tvDetailDynasty.text = "Dynasty / Era: ${topic.dynastyOrParty}"
        } else {
            detailBinding.tvDetailDynasty.visibility = View.GONE
        }

        if (topic.prominentFigures.isNotEmpty()) {
            detailBinding.tvFiguresTitle.visibility = View.VISIBLE
            detailBinding.tvDetailFigures.visibility = View.VISIBLE
            detailBinding.tvDetailFigures.text = topic.prominentFigures.joinToString(", ")
        } else {
            detailBinding.tvFiguresTitle.visibility = View.GONE
            detailBinding.tvDetailFigures.visibility = View.GONE
        }

        if (topic.imageUrl.isNotBlank()) {
            Glide.with(this)
                .load(topic.imageUrl)
                .placeholder(R.drawable.placeholder_heritage)
                .error(R.drawable.placeholder_heritage)
                .centerCrop()
                .into(detailBinding.ivDetailImage)
        } else {
            detailBinding.ivDetailImage.setImageResource(R.drawable.placeholder_heritage)
        }

        // Render Key Facts dynamically
        detailBinding.layoutKeyFactsContainer.removeAllViews()
        topic.keyFacts.forEach { fact ->
            val factTextView = TextView(this).apply {
                text = "• $fact"
                setTextColor(resources.getColor(R.color.text_primary, theme))
                textSize = 13f
                setPadding(0, 4, 0, 8)
            }
            detailBinding.layoutKeyFactsContainer.addView(factTextView)
        }

        detailBinding.btnAskAiTopic.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this@MainActivity, AskHistorianActivity::class.java).apply {
                putExtra("EXTRA_STATE_NAME", topic.location ?: topic.dynastyOrParty ?: "India")
                putExtra("EXTRA_ERA_TITLE", topic.periodEra)
                putExtra("EXTRA_DEFAULT_QUESTION", "Tell me in detail about ${topic.title} (${topic.subtitle}) and its historical impact on India.")
            }
            startActivity(intent)
        }

        if (!topic.stateSlug.isNullOrBlank()) {
            detailBinding.btnExploreStateTopic.visibility = View.VISIBLE
            detailBinding.btnExploreStateTopic.setOnClickListener {
                bottomSheetDialog.dismiss()
                val intent = Intent(this@MainActivity, StateDetailActivity::class.java).apply {
                    putExtra("EXTRA_STATE_SLUG", topic.stateSlug)
                }
                startActivity(intent)
            }
        } else {
            detailBinding.btnExploreStateTopic.visibility = View.GONE
        }

        bottomSheetDialog.show()
    }

    private fun showExplorerHubBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val hubBinding = DialogExplorerHubBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(hubBinding.root)

        hubBinding.cardHubSmritiWalk.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this@MainActivity, SmritiWalkActivity::class.java))
        }

        hubBinding.cardHubAskHistorian.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this@MainActivity, AskHistorianActivity::class.java))
        }

        hubBinding.cardHubCommunity.setOnClickListener {
            bottomSheetDialog.dismiss()
            startActivity(Intent(this@MainActivity, CommunityActivity::class.java))
        }

        bottomSheetDialog.show()
    }

    private fun observeViewModel() {
        viewModel.states.observe(this) { states ->
            if (viewModel.selectedCategory.value == HistoryCategory.STATES) {
                stateAdapter.setStates(states)
                if (states.isNullOrEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvStates.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvStates.visibility = View.VISIBLE
                }
            }
        }

        viewModel.historyTopics.observe(this) { topics ->
            if (viewModel.selectedCategory.value != HistoryCategory.STATES) {
                topicAdapter.setTopics(topics)
                if (topics.isNullOrEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvStates.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvStates.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading == true
            binding.progressBar.visibility = if (isLoading == true && stateAdapter.itemCount == 0 && topicAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
