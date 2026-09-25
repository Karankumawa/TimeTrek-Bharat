package com.example.timetrekbharat.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetrekbharat.adapter.CommunityPostAdapter
import com.example.timetrekbharat.databinding.ActivityCommunityBinding
import com.example.timetrekbharat.viewmodel.CommunityViewModel

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private lateinit var viewModel: CommunityViewModel
    private lateinit var adapter: CommunityPostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewModel = ViewModelProvider(this)[CommunityViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Refresh when coming back from AddPostActivity
        viewModel.fetchPosts(null)
    }

    private fun setupRecyclerView() {
        adapter = CommunityPostAdapter()
        binding.rvCommunityPosts.layoutManager = LinearLayoutManager(this)
        binding.rvCommunityPosts.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchPosts(null)
        }

        binding.fabAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.posts.observe(this) { posts ->
            adapter.setPosts(posts)
            if (posts.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvCommunityPosts.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvCommunityPosts.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading == true
            binding.progressBar.visibility = if (isLoading == true && adapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
