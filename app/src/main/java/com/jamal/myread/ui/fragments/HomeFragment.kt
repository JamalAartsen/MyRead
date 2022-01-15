package com.jamal.myread.ui.fragments

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.getkeepsafe.taptargetview.TapTargetView
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentHomeBinding
import com.jamal.myread.model.MessageEvent
import com.jamal.myread.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val ALERT_DIALOG = "AlertDialog"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    val Context.dataStoreHome: DataStore<Preferences> by preferencesDataStore(name = "firstTime")
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    private val viewModel by viewModels<HomeViewModel>()
    private var pitchSeekbar: Float? = null
    private var speedSeekbar: Float? = null
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.updatePreferencesVoice(requireContext(), pitchSeekbar, speedSeekbar)
                viewModel.startService(
                    requireActivity(),
                    requireContext(),
                    it.resultCode,
                    it.data!!
                )
            }
            Log.d(TAG, "GetResult is called")
        } else {
            Log.d(TAG, "RESULT_OK is false")
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

        viewLifecycleOwner.lifecycleScope.launch {
            if (readDataStore("first_time") == true) {
                TapTargetSequences()
            }
        }

        binding.startButton.setOnClickListener {
            if (viewModel.checkOverlayPermission(requireContext())) {
                speak(binding)
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

    private suspend fun writeDataStore(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        requireContext().dataStoreHome.edit { firstTime ->
            firstTime[dataStoreKey] = value
        }
    }

    private suspend fun readDataStore(key: String): Boolean? {
        val dataStoreKey = booleanPreferencesKey(key)
        val preferences = requireContext().dataStoreHome.data.first()
        return preferences[dataStoreKey]
    }

    fun TapTargetSequences() {
        TapTargetSequence(requireActivity())
            .targets(
                TapTargetView(
                    binding.pitchTitle,
                    "Pitch Voice",
                    "Here you can change the pitch of the voice.",
                    R.color.dark_purple
                ),
                TapTargetView(
                    binding.speedTitle,
                    "Speed Voice",
                    "Here you can change the speed of the voice.",
                    R.color.light_purple
                ),
                TapTargetView(
                    binding.startButton,
                    "Start Button",
                    "Click on the 'START' button to activate the read button!",
                    R.color.dark_purple
                ),
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    viewLifecycleOwner.lifecycleScope.launch {
                        writeDataStore("first_time", false)
                    }
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {

                }

            }).start()
    }

    fun TapTargetView(
        binding: View,
        title: String,
        description: String,
        @ColorRes bgColor: Int
    ): TapTarget? {
        return TapTarget.forView(
            binding,
            title,
            description
        )
            .titleTextSize(24)
            .descriptionTextSize(18)
            .outerCircleColor(bgColor)
            .textTypeface(ResourcesCompat.getFont(requireContext(), R.font.ubuntu_regular))
            .titleTypeface(ResourcesCompat.getFont(requireContext(), R.font.ubuntu_bold))
            .cancelable(false)
            .transparentTarget(true)
            .targetRadius(40)
            .outerCircleAlpha(0.96f)
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

    private fun speak(binding: FragmentHomeBinding) {
        var pitch = (binding.seekbarPitch.progress / 50).toFloat()
        if (pitch < 0.1) pitch = 0.1f

        var speed = (binding.seekbarSpeed.progress / 50).toFloat()
        if (speed < 0.1) speed = 0.1f

        pitchSeekbar = pitch
        speedSeekbar = speed
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
}