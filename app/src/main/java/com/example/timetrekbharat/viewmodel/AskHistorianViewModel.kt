package com.example.timetrekbharat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.ai.GeminiHistorianService

class AskHistorianViewModel(application: Application) : AndroidViewModel(application) {

    private val historianService = GeminiHistorianService()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _currentPrompt = MutableLiveData("")
    val currentPrompt: LiveData<String> = _currentPrompt

    private val _historianAnswer = MutableLiveData("")
    val historianAnswer: LiveData<String> = _historianAnswer

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun askQuestion(stateContext: String?, eraContext: String?, question: String) {
        if (question.isBlank()) {
            _errorMessage.value = "Please enter a question."
            return
        }

        _currentPrompt.value = question.trim()
        _isLoading.value = true
        _errorMessage.value = null

        historianService.askHistorian(stateContext, eraContext, question.trim(), object : GeminiHistorianService.HistorianCallback {
            override fun onSuccess(responseText: String) {
                _isLoading.value = false
                _historianAnswer.value = responseText
            }

            override fun onError(errorMessage: String) {
                _isLoading.value = false
                _errorMessage.value = errorMessage
            }
        })
    }
}
