package com.jamal.myread.ui.fragments

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentHomeBinding
import com.jamal.myread.model.MessageEvent
import com.jamal.myread.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val ALERT_DIALOG = "AlertDialog"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    private val viewModel by viewModels<HomeViewModel>()
    private var pitchSeekbar: Float? = null
    private var speedSeekbar: Float? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "voice_Settings")
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            viewModel.startService(
                requireActivity(),
                requireContext(),
                it.resultCode,
                it.data!!,
                pitchSeekbar,
                speedSeekbar
            )

        } else {
            binding.apply {
                seekbarPitch.isEnabled = true
                seekbarSpeed.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        requireActivity().window.navigationBarColor = ContextCompat.getColor(
            requireContext(),
            R.color.white
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSettingsVoice(binding)

        viewLifecycleOwner.lifecycleScope.launch {
            binding.seekbarSpeed.progress = readVoiceSettings(PreferencesKeys.SPEED).times(50).toInt()
            binding.seekbarPitch.progress = readVoiceSettings(PreferencesKeys.PITCH).times(50).toInt()
        }

        binding.startButton.setOnClickListener {
            if (viewModel.checkOverlayPermission(requireContext())) {
                val mProjectionManager =
                    requireActivity().getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                getResult.launch(mProjectionManager.createScreenCaptureIntent())
                binding.apply {
                    seekbarSpeed.isEnabled = false
                    seekbarPitch.isEnabled = false
                }
            } else {
                AlertDialogFragment().show(parentFragmentManager, ALERT_DIALOG)
            }
        }
    }

    private suspend fun saveVoiceSettings(key: Preferences.Key<Float>, value: Float) {
        requireContext().dataStore.edit { voiceSettings ->
            voiceSettings[key] = value
        }
    }

    private suspend fun readVoiceSettings(key: Preferences.Key<Float>): Float {
        val preferences = requireContext().dataStore.data.first()
        return preferences[key] ?: 1f
    }

    private object PreferencesKeys {
        val PITCH = floatPreferencesKey("pitch")
        val SPEED = floatPreferencesKey("speed")
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        Log.d(TAG, "onMessageEvent: ${event.message}")
        binding.apply {
            seekbarPitch.isEnabled = true
            seekbarSpeed.isEnabled = true
        }
    }

    @Subscribe
    fun handleOtherMessages() {
        Log.d(TAG, "handleOtherMessages: Receive other message")
    }

    /**
     * Set the voice settings (Pitch and Speed)
     *
     * @param binding
     *
     * @author Jamal Aartsen
     */
    private fun setSettingsVoice(binding: FragmentHomeBinding) {
        binding.seekbarSpeed.setOnSeekBarChangeListener(this)
        binding.seekbarPitch.setOnSeekBarChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar.let {
            if (binding.seekbarSpeed === it) {
                speedSeekbar = (progress / 50.0f)
                viewLifecycleOwner.lifecycleScope.launch {
                    saveVoiceSettings(PreferencesKeys.SPEED, progress / 50.0f)
                }
            }

            if (binding.seekbarPitch === it) {
                pitchSeekbar = (progress / 50.0f)
                viewLifecycleOwner.lifecycleScope.launch {
                    saveVoiceSettings(PreferencesKeys.PITCH, progress / 50.0f)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}