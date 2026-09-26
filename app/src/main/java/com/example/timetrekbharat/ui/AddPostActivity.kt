package com.example.timetrekbharat.ui

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timetrekbharat.databinding.ActivityAddPostBinding
import com.example.timetrekbharat.viewmodel.CommunityViewModel

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding
    private lateinit var viewModel: CommunityViewModel

    private val indianStates = arrayOf(
        "Rajasthan", "Gujarat", "Delhi", "Maharashtra", "Uttar Pradesh",
        "Tamil Nadu", "Goa", "Punjab", "Karnataka", "West Bengal",
        "Kerala", "Bihar", "Odisha", "Madhya Pradesh", "Assam", "Telangana"
    )

    private val categories = arrayOf(
        "🏛️ Historical Discovery",
        "🎭 Cultural Event",
        "🏰 Monument Update",
        "📜 Local Legend",
        "🌊 Coastal Heritage"
    )

    private val districtMap = mapOf(
        "Rajasthan" to arrayOf("Jaipur", "Amer", "Udaipur", "Jodhpur", "Chittorgarh", "Bikaner", "Jaisalmer", "Ajmer", "Kota"),
        "Gujarat" to arrayOf("Gandhinagar", "Ahmedabad", "Patan", "Modhera", "Lothal", "Dholavira", "Kutch", "Surat"),
        "Delhi" to arrayOf("New Delhi", "Old Delhi (Shahjahanabad)", "South Delhi", "Qutub Complex", "Central Delhi"),
        "Maharashtra" to arrayOf("Mumbai", "Pune", "Raigad", "Aurangabad (Chhatrapati Sambhajinagar)", "Nagpur", "Kolhapur"),
        "Uttar Pradesh" to arrayOf("Lucknow", "Varanasi (Kashi)", "Agra", "Ayodhya", "Mathura", "Sarnath", "Jhansi"),
        "Tamil Nadu" to arrayOf("Chennai", "Thanjavur", "Madurai", "Mahabalipuram", "Coimbatore", "Kanchipuram"),
        "Goa" to arrayOf("Panaji (North Goa)", "Margao (South Goa)", "Vasco da Gama", "Mapusa", "Ponda", "Calangute"),
        "Punjab" to arrayOf("Amritsar", "Chandigarh", "Ludhiana", "Patiala", "Jalandhar", "Rupnagar (Ropar)"),
        "Karnataka" to arrayOf("Bengaluru", "Hampi (Vijayanagara)", "Mysuru", "Badami", "Belur", "Pattadakal"),
        "West Bengal" to arrayOf("Kolkata", "Darjeeling", "Murshidabad", "Santiniketan", "Sundarbans")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        viewModel = ViewModelProvider(this)[CommunityViewModel::class.java]

        setupDropdowns()

        binding.btnSubmit.setOnClickListener {
            submitPost()
        }

        observeViewModel()
    }

    private fun setupDropdowns() {
        // State Adapter
        val stateAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, indianStates)
        binding.actvState.setAdapter(stateAdapter)

        binding.actvState.setOnItemClickListener { parent, _, position, _ ->
            val selectedState = parent.getItemAtPosition(position).toString()
            updateDistrictSuggestions(selectedState)
        }

        // Category Adapter
        val categoryAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(categoryAdapter)
        binding.actvCategory.setText(categories[0], false)
    }

    private fun updateDistrictSuggestions(state: String) {
        val districts = districtMap[state] ?: arrayOf("City / Local Area")
        val districtAdapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, districts)
        binding.actvLocation.setAdapter(districtAdapter)
        if (districts.isNotEmpty()) {
            binding.actvLocation.setText(districts[0], false)
        }
    }

    private fun submitPost() {
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val location = binding.actvLocation.text.toString().trim()
        val state = binding.actvState.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val author = binding.etAuthor.text.toString().trim()
        val image = binding.etImageUrl.text.toString().trim()

        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Title and Description are required", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Submitting..."

        val stateSlug = if (state.isNotBlank()) state.lowercase().replace(" ", "-") else "general"

        viewModel.addPost(title, desc, category, location, stateSlug, author, image)
    }

    private fun observeViewModel() {
        viewModel.postSuccess.observe(this) { success ->
            if (success == true) {
                Toast.makeText(this, "Update successfully submitted!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Submit"
            }
        }
    }
}
