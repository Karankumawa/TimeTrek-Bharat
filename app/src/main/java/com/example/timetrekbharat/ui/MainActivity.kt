package com.example.timetrekbharat.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.timetrekbharat.adapter.StateAdapter
import com.example.timetrekbharat.databinding.ActivityMainBinding
import com.example.timetrekbharat.viewmodel.MainViewModel

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
        setupSearchAndListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = StateAdapter { state ->
            val intent = Intent(this@MainActivity, StateDetailActivity::class.java).apply {
                putExtra("EXTRA_STATE_SLUG", state.slug)
                putExtra("EXTRA_STATE_NAME", state.name)
            }
            startActivity(intent)
        }
        binding.rvStates.layoutManager = GridLayoutManager(this, 1)
        binding.rvStates.adapter = adapter
    }

    private fun setupSearchAndListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshStates()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.refreshStates()
        }

        binding.etSearchState.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.fabAskHistorian.setOnClickListener {
            startActivity(Intent(this@MainActivity, AskHistorianActivity::class.java))
        }

        binding.fabCommunity.setOnClickListener {
            startActivity(Intent(this@MainActivity, CommunityActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.states.observe(this) { states ->
            adapter.setStates(states)
            if (states.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvStates.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvStates.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading == true
            binding.progressBar.visibility = if (isLoading == true) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
