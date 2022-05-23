package com.jamal.myread.ui.fragments

import android.animation.ObjectAnimator
import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentHomeBinding
import com.jamal.myread.model.MessageEvent
import com.jamal.myread.utils.DataStoreVoiceSettings
import com.jamal.myread.utils.PreferencesKeys
import com.jamal.myread.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import com.jamal.myread.viewmodel.Result
import kotlinx.coroutines.flow.collect

private const val ALERT_DIALOG = "AlertDialog"

/**
 * TODO Context cause memory leak. Because when activity is destroyed it still has a context of
 * TODO activity inside service
 */
@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    private val homeViewModel by viewModels<HomeViewModel>()
    private var pitchSeekbar: Float? = null
    private var speedSeekbar: Float? = null

    @Inject
    lateinit var dataStoreVoiceSettings: DataStoreVoiceSettings
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.checkResult(
                it.resultCode,
                Activity.RESULT_OK,
                requireActivity(),
                requireContext(),
                it.data,
                pitchSeekbar,
                speedSeekbar
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            homeViewModel.showMessageTTSEngines(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.result.collect { result ->
                when (result) {
                    is Result.EnableElements -> {
                        enableElements(true)
                    }
                    is Result.PermissionTrue -> {
                        val mProjectionManager =
                            requireActivity().getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        getResult.launch(mProjectionManager.createScreenCaptureIntent())
                        enableElements(false)
                    }
                    is Result.PermissionFalse -> {
                        AlertDialogFragment().show(parentFragmentManager, ALERT_DIALOG)
                    }
                    is Result.DisableElementsIfServiceRunningAndApp -> enableElements(false)
                    is Result.TTSEngineNotAvailable -> {
                        Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                        enableElements(false)
                        binding.uitlegText.run {
                            text = result.message
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                setTextColor(resources.getColor(R.color.red, null))
                            } else {
                                setTextColor(resources.getColor(R.color.red))
                            }
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.checkIfServiceIsRunningAndAppDisableElements(requireActivity())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            voiceSettingsSeekBarProgressAnimation(
                binding.seekbarSpeed,
                dataStoreVoiceSettings.readVoiceSettings(requireContext(), PreferencesKeys.SPEED)
                    .times(50).toInt()
            )
            voiceSettingsSeekBarProgressAnimation(
                binding.seekbarPitch,
                dataStoreVoiceSettings.readVoiceSettings(requireContext(), PreferencesKeys.PITCH)
                    .times(50).toInt()
            )
        }

        binding.startButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.isPermissionGiven(requireContext())
            }
        }
    }

    /**
     * Gives the seekbar progress animation. It goes from 50 to given value in 250 msec, it gives a
     * smooth  effect.
     *
     * @param seekBar The seekbar where you want to add this animation
     * @param value End station of the animation
     *
     * @author Jamal Aartsen
     */
    private fun voiceSettingsSeekBarProgressAnimation(seekBar: SeekBar, value: Int) {
        ObjectAnimator.ofInt(seekBar, "progress", 50, value).apply {
            duration = 250
            start()
        }
    }

    /**
     * Recieves messages from EventBus to enable the seekbars.
     *
     * @param event MessageEvent with the message
     *
     * @author Jamal Aartsen
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        Log.d(TAG, "onMessageEvent: ${event.message}")
        enableElements(true)
    }


    private fun enableElements(isEnabled: Boolean) {
        binding.apply {
            seekbarPitch.isEnabled = isEnabled
            seekbarSpeed.isEnabled = isEnabled
            startButton.isEnabled = isEnabled
        }
    }

    /**
     * Handles messages that come from the EventBus that doesn't belong to the onMessageEvent
     * method.
     *
     * @author Jamal Aartsen
     */
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        seekBar.let {
            if (binding.seekbarSpeed === it) {
                speedSeekbar = (progress / 50.0f)
                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreVoiceSettings.saveVoiceSettings(
                        requireContext(),
                        PreferencesKeys.SPEED,
                        progress / 50.0f
                    )
                }
            }

            if (binding.seekbarPitch === it) {
                pitchSeekbar = (progress / 50.0f)
                viewLifecycleOwner.lifecycleScope.launch {
                    dataStoreVoiceSettings.saveVoiceSettings(
                        requireContext(),
                        PreferencesKeys.PITCH,
                        progress / 50.0f
                    )
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}