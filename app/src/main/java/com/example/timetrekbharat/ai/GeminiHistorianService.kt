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

    private val modelFutures: GenerativeModelFutures
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private val isApiKeyConfigured: Boolean

    interface HistorianCallback {
        fun onSuccess(responseText: String)
        fun onError(errorMessage: String)
    }

    init {
        val keyToUse = if (!apiKey.isNullOrBlank() && apiKey != "YOUR_GEMINI_API_KEY") {
            apiKey
        } else {
            GEMINI_API_KEY
        }

        isApiKeyConfigured = keyToUse != "YOUR_GEMINI_API_KEY"

        val gm = GenerativeModel(MODEL_NAME, keyToUse)
        modelFutures = GenerativeModelFutures.from(gm)
    }

    fun askHistorian(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String,
        callback: HistorianCallback?
    ) {
        if (callback == null) return

        if (!isApiKeyConfigured) {
            mainHandler.postDelayed({
                val offlineAnswer = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                callback.onSuccess(offlineAnswer)
            }, 800)
            return
        }

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

        val responseFuture: ListenableFuture<GenerateContentResponse> = modelFutures.generateContent(content)

        Futures.addCallback(responseFuture, object : FutureCallback<GenerateContentResponse> {
            override fun onSuccess(result: GenerateContentResponse?) {
                var responseText = result?.text
                if (responseText.isNullOrBlank()) {
                    responseText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                }
                mainHandler.post { callback.onSuccess(responseText) }
            }

            override fun onFailure(t: Throwable) {
                val fallbackText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                mainHandler.post { callback.onSuccess(fallbackText) }
            }
        }, executor)
    }

    private fun generateOfflineHistorianAnswer(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String?
    ): String {
        val state = if (!stateContext.isNullOrBlank()) stateContext else "India"
        val era = if (!eraContext.isNullOrBlank()) eraContext else "Historical Era"

        val sb = StringBuilder()
        sb.append("📜 ").append(state).append(" Historical Analysis (").append(era).append(")\n\n")

        val q = userQuestion?.lowercase() ?: ""

        if (q.contains("fort") || q.contains("architectur")) {
            sb.append("🏛️ Architectural Legacy & Monuments:\n")
            sb.append("• ").append(state).append(" is world-renowned for its sublime architectural synthesis. ")
            sb.append("In the ").append(era).append(", royal patrons commissioned stone fortresses with massive defensive ramparts, intricate stepwells, and ornate carved temples.\n")
            sb.append("• Key Features: Massive stone bastions, double-walled fortifications, rainwater harvesting stepwells, and jali fretwork balconies.")
        } else if (q.contains("ruler") || q.contains("warrior") || q.contains("battle") || q.contains("king")) {
            sb.append("⚔️ Military Dominance & Royalty:\n")
            sb.append("• During the ").append(era).append(" in ").append(state).append(", legendary rulers and military commanders defended territorial sovereignty with strategic valor.\n")
            sb.append("• Key Highlights: Formation of disciplined cavalry and infantry divisions, hill fort vantage points, and diplomatic alliances.")
        } else if (q.contains("cultur") || q.contains("art") || q.contains("tradition")) {
            sb.append("🎨 Cultural Heritage & Artistic Masterpieces:\n")
            sb.append("• The ").append(era).append(" in ").append(state).append(" witnessed a vibrant renaissance in miniature paintings, classical court music, and folk performing arts.\n")
            sb.append("• Royal courts actively patronized scholars, poets, and master artisans, creating lasting cultural traditions celebrated across Bharat today.")
        } else {
            sb.append("🏛️ Historical Summary for ").append(state).append(":\n")
            sb.append("• The ").append(era).append(" represents a foundational period in ").append(state).append("'s timeline. ")
            sb.append("It produced significant socio-economic expansion, flourishing trade routes, and lasting royal dynasties.\n\n")
            sb.append("💡 Note: Configure your Gemini API Key in GeminiHistorianService.kt for live generative AI responses.")
        }

        return sb.toString()
    }

    companion object {
        private const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY"
        private const val MODEL_NAME = "gemini-1.5-flash"
    }
}
