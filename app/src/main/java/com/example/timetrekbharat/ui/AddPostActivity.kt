package com.example.timetrekbharat.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timetrekbharat.databinding.ActivityAddPostBinding
import com.example.timetrekbharat.viewmodel.CommunityViewModel

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var viewModel: CommunityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewModel = ViewModelProvider(this)[CommunityViewModel::class.java]

        binding.btnSubmit.setOnClickListener {
            submitPost()
        }

        observeViewModel()
    }

    private fun submitPost() {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val state = binding.etState.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val image = binding.etImageUrl.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Title and Description are required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Publishing..."

        viewModel.addPost(title, desc, location, state, author, image)
    }

    private fun observeViewModel() {
        viewModel.postSuccess.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Lore successfully added!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Publish Lore"
            }
        }
    }
}
