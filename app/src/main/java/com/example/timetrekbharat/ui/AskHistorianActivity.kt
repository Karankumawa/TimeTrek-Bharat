package com.example.timetrekbharat.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timetrekbharat.databinding.ActivityAskHistorianBinding
import com.example.timetrekbharat.viewmodel.AskHistorianViewModel

class AskHistorianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAskHistorianBinding
    private lateinit var viewModel: AskHistorianViewModel

    private var stateContext: String? = null
    private var eraContext: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAskHistorianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        stateContext = intent.getStringExtra("EXTRA_STATE_NAME")
        eraContext = intent.getStringExtra("EXTRA_ERA_TITLE")
        val defaultQuestion = intent.getStringExtra("EXTRA_DEFAULT_QUESTION")

        if (!stateContext.isNullOrEmpty()) {
            binding.tvContextBadge.text = "Context: $stateContext" + if (!eraContext.isNullOrEmpty()) " ($eraContext)" else ""
        } else {
            binding.tvContextBadge.text = "Context: General Indian History"
        }

        if (!defaultQuestion.isNullOrEmpty()) {
            binding.etQuestionInput.setText(defaultQuestion)
        }

        viewModel = ViewModelProvider(this)[AskHistorianViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.chipPrompt1.setOnClickListener {
            val q = "Tell me about the famous forts, palaces, and architectural marvels of ${stateContext ?: "India"}."
            binding.etQuestionInput.setText(q)
            sendQuestion(q)
        }

        binding.chipPrompt2.setOnClickListener {
            val q = "Who were the legendary rulers, warriors, and key battles in ${stateContext ?: "Indian history"}?"
            binding.etQuestionInput.setText(q)
            sendQuestion(q)
        }

        binding.chipPrompt3.setOnClickListener {
            val q = "What were the major cultural heritage, arts, and traditions during this historical era?"
            binding.etQuestionInput.setText(q)
            sendQuestion(q)
        }

        binding.btnSendQuestion.setOnClickListener {
            val question = binding.etQuestionInput.text.toString()
            sendQuestion(question)
        }
    }

    private fun sendQuestion(question: String?) {
        if (!question.isNullOrBlank()) {
            viewModel.askQuestion(stateContext, eraContext, question)
        } else {
            Toast.makeText(this, "Please type a question.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.pbAiLoading.visibility = if (isLoading == true) View.VISIBLE else View.GONE
            binding.btnSendQuestion.isEnabled = isLoading != true
        }

        viewModel.currentPrompt.observe(this) { prompt ->
            if (!prompt.isNullOrEmpty()) {
                binding.tvUserPromptDisplay.text = "Question: $prompt"
            }
        }

        viewModel.historianAnswer.observe(this) { answer ->
            if (!answer.isNullOrEmpty()) {
                binding.tvAiResponse.text = answer
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(this@AskHistorianActivity, error, Toast.LENGTH_LONG).show()
            }
        }
    }
}
