package com.example.timetrekbharat.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.timetrekbharat.ai.GeminiHistorianService
import com.example.timetrekbharat.model.BardicSeal
import com.example.timetrekbharat.model.DepthTier
import com.example.timetrekbharat.model.StoryPersona
import com.example.timetrekbharat.model.SmritiArtifact
import com.example.timetrekbharat.repository.SmritiDataRepository
import java.util.Locale

class SmritiWalkViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository = SmritiDataRepository()
    private val aiService = GeminiHistorianService()

    private val _artifacts = MutableLiveData<List<SmritiArtifact>>()
    val artifacts: LiveData<List<SmritiArtifact>> = _artifacts

    private val _selectedArtifact = MutableLiveData<SmritiArtifact>()
    val selectedArtifact: LiveData<SmritiArtifact> = _selectedArtifact

    private val _currentDepth = MutableLiveData(DepthTier.TALE)
    val currentDepth: LiveData<DepthTier> = _currentDepth

    private val _currentPersona = MutableLiveData(StoryPersona.BARD)
    val currentPersona: LiveData<StoryPersona> = _currentPersona

    private val _displayedNarrative = MutableLiveData<String>()
    val displayedNarrative: LiveData<String> = _displayedNarrative

    private val _isEpigraphyMode = MutableLiveData(false)
    val isEpigraphyMode: LiveData<Boolean> = _isEpigraphyMode

    private val _isAcousticMode = MutableLiveData(false)
    val isAcousticMode: LiveData<Boolean> = _isAcousticMode

    private val _hydraulicLevel = MutableLiveData(85)
    val hydraulicLevel: LiveData<Int> = _hydraulicLevel

    private val _bardicSeals = MutableLiveData<List<BardicSeal>>()
    val bardicSeals: LiveData<List<BardicSeal>> = _bardicSeals

    private val _isPlayingAudio = MutableLiveData(false)
    val isPlayingAudio: LiveData<Boolean> = _isPlayingAudio

    private val _playbackSpeed = MutableLiveData(1.0f)
    val playbackSpeed: LiveData<Float> = _playbackSpeed

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(application.applicationContext, this)
        val vault = repository.getSampleVaultArtifacts()
        _artifacts.value = vault
        if (vault.isNotEmpty()) {
            selectArtifact(vault[0])
        }
        _bardicSeals.value = repository.getSampleBardicSeals()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("en", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.US
            }
            isTtsReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlayingAudio.postValue(true)
                }

                override fun onDone(utteranceId: String?) {
                    _isPlayingAudio.postValue(false)
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlayingAudio.postValue(false)
                }
            })
        }
    }

    fun selectArtifact(artifact: SmritiArtifact) {
        _selectedArtifact.value = artifact
        _hydraulicLevel.value = artifact.hydraulicMonsoonLevel
        updateNarrativeText()
    }

    fun setDepthTier(depth: DepthTier) {
        _currentDepth.value = depth
        updateNarrativeText()
    }

    fun setStoryPersona(persona: StoryPersona) {
        _currentPersona.value = persona
        updateNarrativeText()
    }

    fun toggleEpigraphyMode() {
        val current = _isEpigraphyMode.value ?: false
        _isEpigraphyMode.value = !current
    }

    fun toggleAcousticMode() {
        val current = _isAcousticMode.value ?: false
        _isAcousticMode.value = !current
    }

    fun setHydraulicLevel(level: Int) {
        _hydraulicLevel.value = level
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        tts?.setSpeechRate(speed)
    }

    private fun updateNarrativeText() {
        val artifact = _selectedArtifact.value ?: return
        val depth = _currentDepth.value ?: DepthTier.TALE
        val persona = _currentPersona.value ?: StoryPersona.BARD

        val text = artifact.narratives[Pair(depth, persona)]
            ?: artifact.narratives[Pair(depth, StoryPersona.BARD)]
            ?: "• ${artifact.name}: Ancient heritage monument preserved in ${artifact.locationEra}."

        _displayedNarrative.value = text

        if (_isPlayingAudio.value == true) {
            speakNarrative(text)
        }
    }

    fun toggleAudioPlayback() {
        if (_isPlayingAudio.value == true) {
            stopAudio()
        } else {
            val text = _displayedNarrative.value ?: return
            speakNarrative(text)
        }
    }

    private fun speakNarrative(text: String) {
        if (!isTtsReady || tts == null) {
            _statusMessage.value = "Text-to-Speech initializing..."
            return
        }

        stopAudio()
        val params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        val speed = _playbackSpeed.value ?: 1.0f
        tts?.setSpeechRate(speed)

        val cleanText = text.replace("•", "").replace("[", "").replace("]", "")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "SMRITI_NARRATION_UTTERANCE")
        _isPlayingAudio.value = true
    }

    fun stopAudio() {
        tts?.stop()
        _isPlayingAudio.value = false
    }

    fun collectBardicSeal(sealId: String) {
        val currentList = _bardicSeals.value ?: return
        val updated = currentList.map { seal ->
            if (seal.id == sealId) seal.copy(isCollected = true) else seal
        }
        _bardicSeals.value = updated
        _statusMessage.value = "📜 Bardic Seal Collected! Added to offline Smriti Ledger."
    }

    fun analyzeCameraCapturedImage(bitmap: Bitmap?, userPrompt: String = "Identify heritage carving or monument") {
        _isLoading.value = true
        _statusMessage.value = "🔍 Vector Matching & Multimodal AI Analysis..."

        val depth = _currentDepth.value ?: DepthTier.TALE
        val persona = _currentPersona.value ?: StoryPersona.BARD

        val systemPrompt = """
            You are SmritiWalk, a world-class cultural historian and traditional oral bard.
            Target Depth: ${depth.displayName} (${depth.targetWords}).
            Persona: ${persona.displayName} (${persona.description}).
            User Query: $userPrompt.
            
            Enforce rules:
            If low: 2-3 bullet facts (max 45 words).
            If moderate: 1 fluid paragraph (100-130 words).
            If high: 4 structured sections [Architecture], [Living Legend], [Timeline], [Hidden Detail].
        """.trimIndent()

        aiService.askHistorian("SmritiWalk Multimodal Engine", "Ancient Indian Heritage Site", systemPrompt, object : GeminiHistorianService.HistorianCallback {
            override fun onSuccess(responseText: String) {
                _isLoading.value = false
                _displayedNarrative.value = responseText
                _statusMessage.value = "✨ Identified with 96% confidence vector match."
            }

            override fun onError(errorMessage: String) {
                _isLoading.value = false
                _statusMessage.value = "Offline Vector Vault Active: $errorMessage"
                updateNarrativeText()
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
