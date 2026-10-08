package com.example.timetrekbharat.ai

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.timetrekbharat.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GeminiHistorianService @JvmOverloads constructor(apiKey: String? = BuildConfig.GEMINI_API_KEY) {

    private var modelFutures: GenerativeModelFutures? = null
    private var fallbackModelFutures: GenerativeModelFutures? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val mainHandler: Handler = Handler(Looper.getMainLooper())

    val isApiKeyConfigured: Boolean
    val configuredKey: String

    interface HistorianCallback {
        fun onSuccess(responseText: String)
        fun onError(errorMessage: String)
    }

    init {
        val rawKey = (apiKey ?: BuildConfig.GEMINI_API_KEY).trim().removeSurrounding("\"").removeSurrounding("'")
        configuredKey = if (rawKey.isNotBlank() && !rawKey.startsWith("YOUR_", ignoreCase = true)) {
            rawKey
        } else if (BuildConfig.GEMINI_API_KEY.isNotBlank() && !BuildConfig.GEMINI_API_KEY.startsWith("YOUR_", ignoreCase = true)) {
            BuildConfig.GEMINI_API_KEY.trim().removeSurrounding("\"").removeSurrounding("'")
        } else {
            ""
        }

        // A key is configured if it is non-blank, not a placeholder, and has valid length
        isApiKeyConfigured = configuredKey.isNotBlank() && configuredKey.length >= 10

        if (isApiKeyConfigured) {
            modelFutures = try {
                val gm = GenerativeModel(PRIMARY_MODEL_NAME, configuredKey)
                GenerativeModelFutures.from(gm)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize primary model $PRIMARY_MODEL_NAME", e)
                null
            }

            fallbackModelFutures = try {
                val fallbackGm = GenerativeModel(FALLBACK_MODEL_NAME, configuredKey)
                GenerativeModelFutures.from(fallbackGm)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize fallback model $FALLBACK_MODEL_NAME", e)
                null
            }
        } else {
            modelFutures = null
            fallbackModelFutures = null
        }
    }

    fun askHistorian(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String,
        callback: HistorianCallback?
    ) {
        if (callback == null) return

        if (userQuestion.isBlank()) {
            mainHandler.post {
                callback.onError("Please enter a valid question.")
            }
            return
        }

        val futures = modelFutures
        if (!isApiKeyConfigured || futures == null) {
            Log.d(TAG, "API Key not configured or model unavailable. Using offline Knowledge Engine.")
            mainHandler.postDelayed({
                val offlineAnswer = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                callback.onSuccess(offlineAnswer)
            }, 300)
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

            executePromptWithFutures(futures, content, stateContext, eraContext, userQuestion, callback, isSecondary = false)
        } catch (e: Exception) {
            Log.e(TAG, "Error building prompt or starting request", e)
            val fallbackText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
            mainHandler.post { callback.onSuccess(fallbackText) }
        }
    }

    private fun executePromptWithFutures(
        activeFutures: GenerativeModelFutures,
        content: Content,
        stateContext: String?,
        eraContext: String?,
        userQuestion: String,
        callback: HistorianCallback,
        isSecondary: Boolean
    ) {
        val responseFuture: ListenableFuture<GenerateContentResponse> = activeFutures.generateContent(content)

        Futures.addCallback(responseFuture, object : FutureCallback<GenerateContentResponse> {
            override fun onSuccess(result: GenerateContentResponse?) {
                var responseText = result?.text
                if (responseText.isNullOrBlank()) {
                    Log.w(TAG, "Gemini returned empty response text. Falling back to offline engine.")
                    responseText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                }
                val finalResponseText = responseText
                mainHandler.post { callback.onSuccess(finalResponseText) }
            }

            override fun onFailure(t: Throwable) {
                Log.e(TAG, "Gemini request failed (isSecondary=$isSecondary): ${t.localizedMessage}", t)

                val secondaryFutures = fallbackModelFutures
                if (!isSecondary && secondaryFutures != null) {
                    Log.i(TAG, "Attempting request with fallback model $FALLBACK_MODEL_NAME...")
                    executePromptWithFutures(secondaryFutures, content, stateContext, eraContext, userQuestion, callback, isSecondary = true)
                } else {
                    val fallbackText = generateOfflineHistorianAnswer(stateContext, eraContext, userQuestion)
                    mainHandler.post { callback.onSuccess(fallbackText) }
                }
            }
        }, executor)
    }

    private fun generateOfflineHistorianAnswer(
        stateContext: String?,
        eraContext: String?,
        userQuestion: String?
    ): String {
        return HistorianKnowledgeEngine.queryKnowledge(stateContext, eraContext, userQuestion ?: "")
    }

    companion object {
        private const val TAG = "GeminiHistorianService"
        private const val PRIMARY_MODEL_NAME = "gemini-1.5-flash"
        private const val FALLBACK_MODEL_NAME = "gemini-pro"
    }
}
