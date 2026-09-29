package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.ai.GeminiHistorianService
import com.example.timetrekbharat.model.ChatMessage

class AskHistorianViewModel(application: Application) : AndroidViewModel(application) {

    private val historianService = GeminiHistorianService()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        // Initial welcome message from Historian AI
        val welcomeMsg = ChatMessage(
            text = "Namaste! I am your AI Historian 📜. Ask me any question about India's royal dynasties, majestic hill forts, key historical battles, or rich cultural traditions.",
            isUser = false
        )
        _messages.value = listOf(welcomeMsg)
    }

    fun askQuestion(stateContext: String?, eraContext: String?, question: String) {
        if (question.isBlank()) {
            _errorMessage.value = "Please enter a question."
            return
        }

        val trimmedQuestion = question.trim()
        val userMsg = ChatMessage(text = trimmedQuestion, isUser = true)

        val currentList = _messages.value.orEmpty().toMutableList()
        currentList.add(userMsg)
        _messages.value = currentList

        _isLoading.value = true
        _errorMessage.value = null

        historianService.askHistorian(stateContext, eraContext, trimmedQuestion, object : GeminiHistorianService.HistorianCallback {
            override fun onSuccess(responseText: String) {
                _isLoading.value = false
                val aiMsg = ChatMessage(text = responseText, isUser = false)
                val updatedList = _messages.value.orEmpty().toMutableList()
                updatedList.add(aiMsg)
                _messages.value = updatedList
            }

            override fun onError(errorMsg: String) {
                _isLoading.value = false
                _errorMessage.value = errorMsg
            }
        })
    }
}
