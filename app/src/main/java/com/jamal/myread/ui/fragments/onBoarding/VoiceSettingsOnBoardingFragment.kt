package com.jamal.myread.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.jamal.myread.R
import com.jamal.myread.databinding.FragmentVoiceSettingsOnboardingBinding
import com.jamal.myread.utils.DataStoreOnBoarding
import com.jamal.myread.utils.PreferencesKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VoiceSettingsOnBoardingFragment : Fragment() {
    private var _binding: FragmentVoiceSettingsOnboardingBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var dataStoreOnBoarding: DataStoreOnBoarding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceSettingsOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNextVoiceSettings.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_viewPagerFragment_to_homeFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            onBoardingFinished()
        }
    }

    private suspend fun onBoardingFinished() {
        dataStoreOnBoarding.saveOnBoardingPreference(
            requireContext(),
            PreferencesKeys.IS_ON_BOARDING_FINISHED,
            true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}