package com.jamal.myread.ui.fragments

import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentHomeBinding
import com.jamal.myread.model.MessageEvent
import com.jamal.myread.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val ALERT_DIALOG = "AlertDialog"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

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

    @Subscribe(threadMode = ThreadMode.MAIN)
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