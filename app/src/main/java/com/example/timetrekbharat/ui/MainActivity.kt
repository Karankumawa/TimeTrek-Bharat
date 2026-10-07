package com.example.timetrekbharat.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetrekbharat.R
import com.example.timetrekbharat.adapter.StateAdapter
import com.example.timetrekbharat.databinding.ActivityMainBinding
import com.example.timetrekbharat.databinding.DialogExplorerHubBinding
import com.example.timetrekbharat.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: StateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()
        setupSearchAndFilterListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = StateAdapter(
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
        binding.rvStates.layoutManager = LinearLayoutManager(this)
        binding.rvStates.adapter = adapter
    }

    private fun setupSearchAndFilterListeners() {
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

        binding.chipGroupRegions.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            val filter = when (checkedIds[0]) {
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

        binding.tvHeaderBadge.setOnClickListener {
            val count = adapter.itemCount
            Toast.makeText(this, "🏛️ Currently displaying $count Indian States & UTs from live database.", Toast.LENGTH_SHORT).show()
        }
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
            adapter.setStates(states)
            if (states.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvStates.visibility = View.GONE
                binding.tvHeaderBadge.text = "🏛️ 0 States"
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvStates.visibility = View.VISIBLE
                binding.tvHeaderBadge.text = "🏛️ ${states.size} States & UTs"
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading == true
            binding.progressBar.visibility = if (isLoading == true && adapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
