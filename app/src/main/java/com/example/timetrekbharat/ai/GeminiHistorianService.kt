package com.example.timetrekbharat.ai

import android.os.Handler
import android.os.Looper
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GeminiHistorianService @JvmOverloads constructor(apiKey: String? = GEMINI_API_KEY) {

    private var modelFutures: GenerativeModelFutures? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val isApiKeyConfigured: Boolean

    interface HistorianCallback {
        fun onSuccess(responseText: String)
        fun onError(errorMessage: String)
    }

    init {
        val keyToUse = if (!apiKey.isNullOrBlank() && !apiKey.startsWith("YOUR_")) {
            apiKey
        } else {
            GEMINI_API_KEY
        }

        isApiKeyConfigured = keyToUse.isNotBlank() && keyToUse.startsWith("AIza")

        if (isApiKeyConfigured) {
            try {
                val gm = GenerativeModel(MODEL_NAME, keyToUse)
                modelFutures = GenerativeModelFutures.from(gm)
            } catch (_: Exception) {
                modelFutures = null
            }
        } else {
            modelFutures = null
        }
    }

    fun askHistorian(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String,
        callback: HistorianCallback?
    ) {
        if (callback == null) return

        if (!isApiKeyConfigured || modelFutures == null) {
            mainHandler.postDelayed({
                val offlineAnswer = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                callback.onSuccess(offlineAnswer)
            }, 500)
            return
        }

        try {
            val promptBuilder = StringBuilder().apply {
                append("You are an expert, respectful, and authoritative Indian Historian and Scholar.\n")
                append("Your goal is to answer questions about Indian history, dynasties, rulers, architecture, and culture accurately.\n\n")

                if (!stateContext.isNullOrBlank()) {
                    append("State Context: ").append(stateContext).append("\n")
                }
                if (!eraContext.isNullOrBlank()) {
                    append("Historical Era Context: ").append(eraContext).append("\n")
                }

                append("User Question: ").append(userQuestion).append("\n\n")
                append("Provide a clear, engaging, historically structured response highlighting key dynasties, architectural features, rulers, or historical impacts.")
            }

            val content = Content.Builder()
                .text(promptBuilder.toString())
                .build()

            val responseFuture: ListenableFuture<GenerateContentResponse> = modelFutures!!.generateContent(content)

            Futures.addCallback(responseFuture, object : FutureCallback<GenerateContentResponse> {
                override fun onSuccess(result: GenerateContentResponse?) {
                    var responseText = result?.text
                    if (responseText.isNullOrBlank()) {
                        responseText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                    }
                    mainHandler.post { callback.onSuccess(responseText) }
                }

                override fun onFailure(t: Throwable) {
                    // Failover gracefully for any auth, network, or quota error
                    val fallbackText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                    mainHandler.post { callback.onSuccess(fallbackText) }
                }
            }, executor)
        } catch (_: Exception) {
            val fallbackText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
            mainHandler.post { callback.onSuccess(fallbackText) }
        }
    }

    private fun generateOfflineHistorianAnswer(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String?
    ): String {
        return HistorianKnowledgeEngine.queryKnowledge(stateContext, eraContext, userQuestion ?: "")
    }

    companion object {
        private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY"
        private const val MODEL_NAME = "gemini-1.5-flash"
    }
}
