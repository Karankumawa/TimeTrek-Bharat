package com.example.timetrekbharat.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ActivitySmritiWalkBinding
import com.example.timetrekbharat.model.DepthTier
import com.example.timetrekbharat.model.StoryPersona
import com.example.timetrekbharat.viewmodel.SmritiWalkViewModel

class SmritiWalkActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmritiWalkBinding
    private lateinit var viewModel: SmritiWalkViewModel

    private val speedOptions = listOf(0.85f, 1.0f, 1.25f, 1.5f)
    private var speedIndex = 1

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                try {
                    val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                    binding.ivArtifactPreview.setImageBitmap(bitmap)
                    viewModel.analyzeCameraCapturedImage(bitmap, "Identify heritage carving or monument in camera scan")
                } catch (e: Exception) {
                    Toast.makeText(this, "Unable to process image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmritiWalkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SmritiWalkViewModel::class.java]

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnScanCamera.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            selectImageLauncher.launch(intent)
        }

        binding.btnSelectVault.setOnClickListener {
            showSiteVaultSelectorDialog()
        }

        binding.chipGroupDepth.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            when (checkedIds[0]) {
                R.id.chipDepthQuick -> viewModel.setDepthTier(DepthTier.QUICK)
                R.id.chipDepthTale -> viewModel.setDepthTier(DepthTier.TALE)
                R.id.chipDepthChronicle -> viewModel.setDepthTier(DepthTier.CHRONICLE)
            }
        }

        binding.chipGroupPersona.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            when (checkedIds[0]) {
                R.id.chipPersonaBard -> viewModel.setStoryPersona(StoryPersona.BARD)
                R.id.chipPersonaArchitect -> viewModel.setStoryPersona(StoryPersona.ARCHITECT)
                R.id.chipPersonaSentry -> viewModel.setStoryPersona(StoryPersona.SENTRY)
                R.id.chipPersonaChronicler -> viewModel.setStoryPersona(StoryPersona.CHRONICLER)
            }
        }

        binding.btnPlayAudio.setOnClickListener {
            viewModel.toggleAudioPlayback()
        }

        binding.btnSpeedToggle.setOnClickListener {
            speedIndex = (speedIndex + 1) % speedOptions.size
            val newSpeed = speedOptions[speedIndex]
            viewModel.setPlaybackSpeed(newSpeed)
            binding.btnSpeedToggle.text = "${newSpeed}x"
        }

        binding.btnDecipherEpigraphy.setOnClickListener {
            viewModel.toggleEpigraphyMode()
        }

        binding.btnAcousticEcho.setOnClickListener {
            viewModel.toggleAcousticMode()
        }

        binding.sliderHydraulicLevel.addOnChangeListener { _, value, _ ->
            val level = value.toInt()
            viewModel.setHydraulicLevel(level)
            binding.tvHydraulicLevelLabel.text = "Subterranean Level ($level%)"
        }

        binding.btnCollectSeal.setOnClickListener {
            val currentSeals = viewModel.bardicSeals.value
            if (!currentSeals.isNullOrEmpty()) {
                viewModel.collectBardicSeal(currentSeals[0].id)
            }
        }
    }

    private fun showSiteVaultSelectorDialog() {
        val artifacts = viewModel.artifacts.value ?: return
        val names = artifacts.map { "${it.name} (${it.locationEra.split("•")[0].trim()})" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Offline Smriti Vault")
            .setItems(names) { _, which ->
                val selected = artifacts[which]
                viewModel.selectArtifact(selected)
                binding.ivArtifactPreview.setImageResource(R.drawable.app_logo)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.selectedArtifact.observe(this) { artifact ->
            if (artifact != null) {
                binding.tvArtifactName.text = artifact.name
                binding.tvArtifactLocation.text = artifact.locationEra
                binding.tvEpigraphyOriginal.text = artifact.epigraphyOriginal ?: "𑀅𑀥𑀺𑀧𑀢𑀺 𑀭𑀸𑀡𑀸..."
                binding.tvEpigraphyDeciphered.text = artifact.epigraphyDeciphered ?: "Deciphered Inscription"
            }
        }

        viewModel.displayedNarrative.observe(this) { narrative ->
            binding.tvNarrativeText.text = narrative ?: ""
        }

        viewModel.isPlayingAudio.observe(this) { isPlaying ->
            if (isPlaying == true) {
                binding.btnPlayAudio.text = "⏸ Pause"
                binding.tvSoundwaveVisualizer.text = "░▒▓█ 🔊 AUDIO PLAYING █▓▒░"
            } else {
                binding.btnPlayAudio.text = "▶ Listen"
                binding.tvSoundwaveVisualizer.text = "░▒▓█ SOUNDWAVE █▓▒░"
            }
        }

        viewModel.isEpigraphyMode.observe(this) { isVisible ->
            binding.cardEpigraphyOverlay.visibility = if (isVisible == true) View.VISIBLE else View.GONE
        }

        viewModel.isAcousticMode.observe(this) { isAcoustic ->
            if (isAcoustic == true) {
                val artifact = viewModel.selectedArtifact.value
                val desc = artifact?.acousticResonanceDesc ?: "Resonance Frequency Active: 432 Hz Convolution Reverb"
                Toast.makeText(this, "🔊 Acoustic Archaeology Active: $desc", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.bardicSeals.observe(this) { seals ->
            if (!seals.isNullOrEmpty()) {
                val seal = seals.firstOrNull { it.isCollected } ?: seals[0]
                binding.tvBardicSealQuote.text = "\"${seal.audioQuote}\" — ${seal.tellerName} (${if (seal.isCollected) "Collected ✅" else "Uncollected"})"
            }
        }

        viewModel.statusMessage.observe(this) { msg ->
            if (!msg.isNullOrEmpty()) {
                binding.tvStatusMessage.text = msg
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopAudio()
    }
}
